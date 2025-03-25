package com.ptl.linkschecker.core;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.ProgressCounter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class LinksCrawler {

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;

    public void processSite(String startUrl, ProgressCounter progressCounter) throws InterruptedException {
        linksManager.reset();
        linksManager.addNewLinks(Collections.singletonList(startUrl));
        String urlToCheck;
        do {
            urlToCheck = linksManager.getNextUnProcessedLink();
            if (urlToCheck != null) {
                PageResult pageResult;
                String realUrl;
                if (urlToCheck.startsWith("/")) {
                    realUrl = startUrl + urlToCheck;
                } else {
                    realUrl = urlToCheck;
                }
                pageResult = contentRetriever.retrievePageContent(realUrl);
                if (realUrl.startsWith(startUrl)) {
                    List<String> newLinks = linkRetriever.retrieveBodyLinks(pageResult);
                    linksManager.addNewLinks(newLinks);
                }
                linksManager.updateLink(urlToCheck, pageResult.content(), pageResult.httpStatusCode());
                progressCounter.tick();
            }
        } while ( linksManager.getNextUnProcessedLink() != null );
    }

    public List<PageResult> getLinks() {
        return linksManager.getLinks().stream().sorted().toList();
    }

}
