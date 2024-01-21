package com.ptl.linkschecker.core;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.exceptions.LinksCrawlerException;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.LinksClassifier;
import com.ptl.linkschecker.utils.ProgressCounter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class LinksCrawlerImpl implements LinksCrawler{

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;

    public void processSite(String startUrl, ProgressCounter progressCounter) throws LinksCrawlerException {
        // READY
        linksManager.reset();
        linksManager.addNewLinks(Collections.singletonList(startUrl));
        // Populate with a few links
        processOneUrl(startUrl, progressCounter);
        // GO for //
        try (ExecutorService vte = Executors.newVirtualThreadPerTaskExecutor()){
            vte.submit( () -> processOneUrl(startUrl, progressCounter));
        }
        // Check for completeness
        if (linksManager
                .getLinks().stream().anyMatch(pageResult -> LinksClassifier.isUntestedLink(pageResult.httpStatusCode()))
                ){
            throw new LinksCrawlerException("Remaining unprocessed links");
        }
    }

    @SneakyThrows
    private void processOneUrl(String startUrl, ProgressCounter progressCounter) {
        String urlToCheck;
        do {
            urlToCheck = linksManager.getNextUnProcessedLink();
            if (urlToCheck != null ) {
                PageResult pageResult;
                String realUrl = urlToCheck.startsWith("/") ? startUrl + urlToCheck : urlToCheck;
                try {
                    pageResult = contentRetriever.retrievePageContent(realUrl);
                    if (realUrl.startsWith(startUrl)) {
                        List<String> newLinks = linkRetriever.retrieveBodyLinks(pageResult);
                        linksManager.addNewLinks(newLinks);
                    }
                    linksManager.updateLink(urlToCheck, pageResult.content(), pageResult.httpStatusCode());
                } catch (InterruptedException e) {
                    linksManager.updateLink(urlToCheck, java.util.Optional.empty(), 500);
                }
                progressCounter.thick();
            }
        } while ( urlToCheck != null );
    }

    @Override
    public List<PageResult> getLinks() {
        return linksManager.getLinks().stream().sorted().toList();
    }

}
