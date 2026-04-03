package com.ptl.linkschecker.core;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.ProgressCounter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

public class LinksCrawler {

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;
    private final Semaphore semaphore;

    public LinksCrawler(ContentRetriever contentRetriever, LinkRetriever linkRetriever, LinksManager linksManager, int maxParallelRequests) {
        this.contentRetriever = contentRetriever;
        this.linkRetriever = linkRetriever;
        this.linksManager = linksManager;
        this.semaphore = new Semaphore(maxParallelRequests);
    }

    public void processSite(String startUrl, ProgressCounter progressCounter) {
        linksManager.reset();
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
                semaphore.acquire();
                PageResult pageResult;
                try {
                    pageResult = contentRetriever.retrievePageContent(realUrl);
                } finally {
                    semaphore.release();
                }
                if (realUrl.startsWith(startUrl)) {
                    List<String> newLinks = linkRetriever.retrieveBodyLinks(pageResult);
                    newLinks.forEach(link -> submitLink(link, startUrl, executor, phaser, progressCounter));
                }
                linksManager.updateLink(url, pageResult.content(), pageResult.httpStatusCode());
                progressCounter.tick();
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    public List<PageResult> getLinks() {
        return linksManager.getLinks().stream().sorted().toList();
    }
}
