package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LinkListCommand {

    private final LinksCrawler linksCrawler;

    public LinkListCommand(LinksCrawler linksCrawler) {
        this.linksCrawler = linksCrawler;
    }

    public String badLinks() {
        return linksCrawler.getLinks().stream()
                .filter(r -> LinksClassifier.isBadLink(r.httpStatusCode()))
                .map(r -> r.url() + " -> " + r.httpStatusCode())
                .collect(Collectors.joining("\n"));
    }

    public String movedLinks() {
        return linksCrawler.getLinks().stream()
                .filter(r -> LinksClassifier.isRedirectLink(r.httpStatusCode()))
                .map(PageResult::url)
                .collect(Collectors.joining("\n"));
    }
}
