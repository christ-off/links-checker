package com.ptl.linkschecker.core;

import com.ptl.linkschecker.config.SkippedSites;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.LinksClassifier;
import com.ptl.linkschecker.utils.ProgressCounter;
import com.ptl.linkschecker.utils.UrlUtils;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class LinksCrawler {

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;
    private final SkippedSites skippedSites;
    private final int maxConcurrentHosts;

    public LinksCrawler(ContentRetriever contentRetriever, LinkRetriever linkRetriever, LinksManager linksManager, SkippedSites skippedSites) {
        this(contentRetriever, linkRetriever, linksManager, skippedSites, 8);
    }

    public LinksCrawler(ContentRetriever contentRetriever, LinkRetriever linkRetriever, LinksManager linksManager,
                         SkippedSites skippedSites, int maxConcurrentHosts) {
        this.contentRetriever = contentRetriever;
        this.linkRetriever = linkRetriever;
        this.linksManager = linksManager;
        this.skippedSites = skippedSites;
        this.maxConcurrentHosts = maxConcurrentHosts;
    }

    /**
     * Crawls in waves: each wave fetches all pending links on virtual threads,
     * grouped by host so a host never sees more than one request at a time.
     * LinksManager is only touched from the calling thread.
     */
    public void processSite(String startUrl, ProgressCounter progressCounter) {
        UrlUtils.validateScheme(startUrl);
        linksManager.reset();
        linksManager.addNewLinks(List.of(startUrl));

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<String> batch;
            while (!Thread.currentThread().isInterrupted()
                    && !(batch = nextBatch(progressCounter)).isEmpty()) {
                crawlBatch(executor, batch, startUrl, progressCounter);
            }
        }
    }

    private List<String> nextBatch(ProgressCounter progressCounter) {
        List<String> batch = new ArrayList<>();
        String url;
        while ((url = linksManager.getNextUnProcessedLink()) != null) {
            if (skippedSites.isSkipped(url)) {
                progressCounter.tick(0, false, true, false);
            } else {
                batch.add(url);
            }
        }
        return batch;
    }

    private void crawlBatch(ExecutorService executor, List<String> batch, String startUrl, ProgressCounter progressCounter) {
        List<Callable<List<FetchOutcome>>> tasksPerHost = batch.stream()
                .collect(Collectors.groupingBy(UrlUtils::extractHost))
                .values().stream()
                .map(urls -> (Callable<List<FetchOutcome>>) () -> fetchOneByOne(urls, startUrl, progressCounter))
                .toList();
        try {
            // fired in bounded chunks: hitting dozens of distinct hosts at once causes self-inflicted
            // timeouts (TLS/DNS contention) that get misreported as dead links
            for (List<Callable<List<FetchOutcome>>> chunk : partition(tasksPerHost, maxConcurrentHosts)) {
                for (Future<List<FetchOutcome>> hostOutcomes : executor.invokeAll(chunk)) {
                    for (FetchOutcome outcome : hostOutcomes.get()) {
                        linksManager.addNewLinks(outcome.newLinks());
                        linksManager.updateLink(outcome.url(), outcome.location(), outcome.httpStatusCode());
                    }
                }
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new IllegalStateException("Crawl task failed", e.getCause());
        }
    }

    private static <T> List<List<T>> partition(List<T> items, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < items.size(); i += chunkSize) {
            chunks.add(items.subList(i, Math.min(i + chunkSize, items.size())));
        }
        return chunks;
    }

    /** Fetches the URLs of a single host one after another, so the host never sees concurrent requests. */
    private List<FetchOutcome> fetchOneByOne(List<String> urls, String startUrl, ProgressCounter progressCounter) {
        List<FetchOutcome> outcomes = new ArrayList<>(urls.size());
        for (String url : urls) {
            PageResult pageResult;
            try {
                pageResult = contentRetriever.retrievePageContent(url);
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
                break;
            }
            boolean internal = url.startsWith(startUrl);
            List<String> newLinks = internal ? linkRetriever.retrieveBodyLinks(pageResult) : List.of();
            // keep only the redirect Location; retaining page bodies would hold the whole site in memory
            String location = LinksClassifier.isRedirectLink(pageResult.httpStatusCode()) ? pageResult.content() : null;
            outcomes.add(new FetchOutcome(url, location, pageResult.httpStatusCode(), newLinks));
            progressCounter.tick(pageResult.httpStatusCode(), internal, false, url.contains("%"));
        }
        return outcomes;
    }

    private record FetchOutcome(String url, @Nullable String location, int httpStatusCode, List<String> newLinks) {
    }

    public Map<String, Long> getQueriesPerHost() {
        return linksManager.getLinks().stream()
                .collect(Collectors.groupingBy(
                        r -> UrlUtils.extractHost(r.url()),
                        Collectors.counting()));
    }

    public List<PageResult> getLinks() {
        return linksManager.getLinks().stream().sorted().toList();
    }
}