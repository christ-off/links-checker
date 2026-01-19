package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BadCommand {

    private final LinksCrawler linksCrawler;

    public BadCommand(LinksCrawler linksCrawler) {
        this.linksCrawler = linksCrawler;
    }

    @Command( name = "bad", description = "Get all bad links")
    public String bad() {
        return linksCrawler
                .getLinks()
                .stream()
                .filter( pageResult -> LinksClassifier.isBadLink(pageResult.httpStatusCode()))
                .map( pageResult -> pageResult.url() + " -> " + pageResult.httpStatusCode())
                .collect(Collectors.joining("\n"));
    }
}