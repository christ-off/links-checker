package com.ptl.linkschecker.core;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.ProgressCounter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LinksCrawlerImpl implements LinksCrawler{

    private final ContentRetriever contentRetriever;
    private final LinkRetriever linkRetriever;
    private final LinksManager linksManager;

    public void processSite(String startUrl, ProgressCounter progressCounter) throws IOException, InterruptedException {
        linksManager.reset();
        linksManager.addNewLinks(Collections.singletonList(startUrl));
        Optional<String> urlToCheck;
        int count = 0;
        do {
            urlToCheck = linksManager.getNextUnProcessedLink();
            if (urlToCheck.isPresent()) {
                PageResult pageResult;
                String realUrl;
                if (urlToCheck.get().startsWith("/")) {
                    realUrl = startUrl + urlToCheck.get();
                    pageResult = contentRetriever.retrievePageContent(realUrl);
                } else if (urlToCheck.get().startsWith("#")){
                    realUrl = startUrl + urlToCheck.get();
                    pageResult = new PageResult("", 200);
                } else {
                    realUrl = urlToCheck.get();
                    pageResult = contentRetriever.retrievePageContent(realUrl);
                }
                if (realUrl.startsWith(startUrl)) {
                    List<String> newLinks = linkRetriever.retrieveBodyLinks(pageResult);
                    linksManager.addNewLinks(newLinks);
                }
                linksManager.updateLink(urlToCheck.get(), pageResult.httpStatusCode());
                progressCounter.display(count++, "Processing");
            }
        } while ( linksManager.getNextUnProcessedLink().isPresent() );
        progressCounter.reset();
    }

    @Override
    public List<String> getAllBadLinks() {
        return linksManager.getAllBadLinks();
    }

    @Override
    public List<String> getAllGoodLinks() {
        return linksManager.getAllGoodLinks();
    }
}
