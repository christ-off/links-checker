package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MovedCommand {

    public MovedCommand(LinksCrawler linksCrawler) {
        this.linksCrawler = linksCrawler;
    }

    private final LinksCrawler linksCrawler;

    @Command( name = "moved", description = "Get all moved links")
    public String good() {
        return linksCrawler
                .getLinks()
                .stream()
                .filter( pageResult -> LinksClassifier.isRedirectLink(pageResult.httpStatusCode()))
                .map( PageResult::url)
                .collect(Collectors.joining("\n"));
    }
}
