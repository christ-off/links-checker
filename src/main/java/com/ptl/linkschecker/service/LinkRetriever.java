package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import com.ptl.linkschecker.utils.UrlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.List;

/**
 * Extracts links from HTML content, resolved to absolute URLs.
 */
public class LinkRetriever {

    /**
     * Extracts all crawlable links from the HTML body of a page.
     * Relative links are resolved against the page URL, fragments are stripped,
     * and non-HTTP(S) links (mailto:, javascript:, ...) are ignored.
     *
     * @param pageResult The page result containing URL, content, and status code
     * @return absolute links found in the page, or empty list if page is invalid
     */
    public List<String> retrieveBodyLinks(PageResult pageResult) {
        String content = pageResult.content();
        if (content == null || !LinksClassifier.isGoodLink(pageResult.httpStatusCode())) {
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(content, pageResult.url());
        return doc.select("a[href]").stream()
                .filter(link -> isCrawlable(link.attr("href")))
                .map(link -> link.attr("abs:href"))
                .map(link -> link.split("#", 2)[0])
                .filter(UrlUtils::isHttpUrl)
                .toList();
    }

    /** Empty and fragment-only hrefs resolve to the page itself and must not be followed. */
    private boolean isCrawlable(String href) {
        String trimmed = href.trim();
        return !trimmed.isEmpty() && !trimmed.startsWith("#");
    }
}
