package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of LinkRetriever that extracts links from HTML content.
 */
public class LinkRetriever {

    private static final int HTTP_OK_MIN = 200;
    private static final int HTTP_OK_MAX = 300;

    /**
     * Extracts all valid links from the HTML body of a page.
     *
     * @param pageResult The page result containing URL, content, and status code
     * @return List of links found in the page, or empty list if page is invalid
     */
    public List<String> retrieveBodyLinks(PageResult pageResult) {
        // Return empty list if page is not valid or has no content
        if (!isValidPage(pageResult)) {
            return Collections.emptyList();
        }

        // Parse HTML and extract links
        Document doc = Jsoup.parse(pageResult.content().get());
        Elements links = doc.select("a");

        return links.eachAttr("href")
                .stream()
                .filter(link -> !link.startsWith("#"))
                .map(link -> link.split("#", 2)[0])
                .filter(link -> !link.trim().isEmpty())
                .toList();
    }

    /**
     * Checks if the page has a successful status code and contains content.
     */
    private boolean isValidPage(PageResult pageResult) {
        return pageResult != null
                && pageResult.httpStatusCode() >= HTTP_OK_MIN 
                && pageResult.httpStatusCode() < HTTP_OK_MAX
                && pageResult.content().isPresent();
    }
}
