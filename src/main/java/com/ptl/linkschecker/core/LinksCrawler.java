package com.ptl.linkschecker.core;

import com.ptl.linkschecker.config.SkippedSites;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.ProgressCounter;
import com.ptl.linkschecker.utils.UrlUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinksCrawler {

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;
    private final SkippedSites skippedSites;

    public LinksCrawler(ContentRetriever contentRetriever, LinkRetriever linkRetriever, LinksManager linksManager, SkippedSites skippedSites) {
        this.contentRetriever = contentRetriever;
        this.linkRetriever = linkRetriever;
        this.linksManager = linksManager;
        this.skippedSites = skippedSites;
    }

    public void processSite(String startUrl, ProgressCounter progressCounter) {
        UrlUtils.validateScheme(startUrl);
        linksManager.reset();
        linksManager.addNewLinks(List.of(startUrl));

        String url;
        while ((url = linksManager.getNextUnProcessedLink()) != null) {
            String realUrl = url.startsWith("/") ? startUrl + url : url;
            if (skippedSites.isSkipped(realUrl)) {
                progressCounter.tick(0, false, true);
                continue;
            }
            PageResult pageResult;
            try {
                pageResult = contentRetriever.retrievePageContent(realUrl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (realUrl.startsWith(startUrl)) {
                linksManager.addNewLinks(linkRetriever.retrieveBodyLinks(pageResult));
            }
            linksManager.updateLink(url, pageResult.content(), pageResult.httpStatusCode());
            progressCounter.tick(pageResult.httpStatusCode(), realUrl.startsWith(startUrl), false);
        }
    }

    public Map<String, Long> getQueriesPerHost() {
        return linksManager.getLinks().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> UrlUtils.extractHost(r.url()),
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
}
