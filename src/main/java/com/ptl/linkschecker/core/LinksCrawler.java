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

public class LinksCrawler {

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;

    public LinksCrawler(ContentRetriever contentRetriever, LinkRetriever linkRetriever, LinksManager linksManager) {
        this.contentRetriever = contentRetriever;
        this.linkRetriever = linkRetriever;
        this.linksManager = linksManager;
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
                PageResult pageResult = contentRetriever.retrievePageContent(realUrl);
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
