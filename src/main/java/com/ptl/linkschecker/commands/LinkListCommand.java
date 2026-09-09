package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinkListCommand {

    public String badLinks(List<PageResult> links) {
        return links.stream()
                .filter(r -> LinksClassifier.isBadLink(r.httpStatusCode()))
                .map(r -> r.url() + " -> " + r.httpStatusCode())
                .collect(Collectors.joining("\n"));
    }

    public String movedLinks(List<PageResult> links) {
        return links.stream()
                .filter(r -> LinksClassifier.isRedirectLink(r.httpStatusCode()))
                .map(r -> r.content() == null ? r.url() : r.url() + " -> " + r.content())
                .collect(Collectors.joining("\n"));
    }

    public String internalLinksWithPercent(List<PageResult> links, String startUrl) {
        return links.stream()
                .filter(r -> r.url().startsWith(startUrl))
                .filter(r -> r.url().contains("%"))
                .map(PageResult::url)
                .collect(Collectors.joining("\n"));
    }

    public String internalLinksWithoutTrailingSlash(List<PageResult> links, String startUrl) {
        return links.stream()
                .filter(r -> r.url().startsWith(startUrl))
                .filter(r -> LinksClassifier.isGoodLink(r.httpStatusCode()))
                .filter(LinkListCommand::isPageMissingTrailingSlash)
                .map(PageResult::url)
                .collect(Collectors.joining("\n"));
    }

    /** A "page" is a path whose last segment has no file extension; those never need a trailing slash. */
    private static boolean isPageMissingTrailingSlash(PageResult r) {
        String path = URI.create(r.url()).getRawPath();
        if (path == null || path.isEmpty() || path.equals("/") || path.endsWith("/")) {
            return false;
        }
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return !lastSegment.contains(".");
    }
}
