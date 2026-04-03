package com.ptl.linkschecker.core;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.ProgressCounter;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

public class LinksCrawler {

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;
    private final int maxConnectionsPerHost;
    private final ConcurrentHashMap<String, Semaphore> hostSemaphores = new ConcurrentHashMap<>();
    private final Semaphore globalSemaphore;

    public LinksCrawler(ContentRetriever contentRetriever, LinkRetriever linkRetriever, LinksManager linksManager, int maxConnectionsPerHost, int maxParallelRequests) {
        this.contentRetriever = contentRetriever;
        this.linkRetriever = linkRetriever;
        this.linksManager = linksManager;
        this.maxConnectionsPerHost = maxConnectionsPerHost;
        this.globalSemaphore = new Semaphore(maxParallelRequests);
    }

    public void processSite(String startUrl, ProgressCounter progressCounter) {
        // Validate start URL scheme to prevent SSRF/path traversal at entry point
        validateUrlScheme(startUrl);
        linksManager.reset();
        hostSemaphores.clear();
        Phaser phaser = new Phaser(1);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            submitLink(startUrl, startUrl, executor, phaser, progressCounter);
            phaser.arriveAndDeregister();
            phaser.awaitAdvance(0);
        }
    }

    private void submitLink(String url, String startUrl, ExecutorService executor, Phaser phaser, ProgressCounter progressCounter) {
        if (!linksManager.tryAdd(url)) return;
        phaser.register();
        executor.submit(() -> {
            try {
                String realUrl = url.startsWith("/") ? startUrl + url : url;
                Semaphore hostSemaphore = hostSemaphores.computeIfAbsent(extractHost(realUrl), _ -> new Semaphore(maxConnectionsPerHost));
                globalSemaphore.acquire();
                hostSemaphore.acquire();
                PageResult pageResult;
                try {
                    pageResult = contentRetriever.retrievePageContent(realUrl);
                } finally {
                    hostSemaphore.release();
                    globalSemaphore.release();
                }
                if (realUrl.startsWith(startUrl)) {
                    List<String> newLinks = linkRetriever.retrieveBodyLinks(pageResult);
                    newLinks.forEach(link -> submitLink(link, startUrl, executor, phaser, progressCounter));
                }
                linksManager.updateLink(url, pageResult.content(), pageResult.httpStatusCode());
                progressCounter.tick(pageResult.httpStatusCode(), realUrl.startsWith(startUrl));
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    private String extractHost(String url) {
        try {
            String host = URI.create(url).getHost();
            return host != null ? host : url;
        } catch (IllegalArgumentException _) {
            return url;
        }
    }

    public Map<String, Long> getQueriesPerHost() {
        return linksManager.getLinks().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> extractHost(r.url()),
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public List<PageResult> getLinks() {
        return linksManager.getLinks().stream().sorted().toList();
    }

    private void validateUrlScheme(String url) {
        try {
            var uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme != null && !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                throw new IllegalArgumentException("Only HTTP and HTTPS URLs are allowed (SSRF protection)");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid or unsafe URL scheme: " + e.getMessage(), e);
        }
    }
}
