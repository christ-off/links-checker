package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
public class GoodCommand {

    private final LinksCrawler linksCrawler;

    public GoodCommand(LinksCrawler linksCrawler) {
        this.linksCrawler = linksCrawler;
    }

    @ShellMethod(key = "good", value = "Get all good links")
    public String good() {
        return linksCrawler
                .getLinks()
                .stream()
                .filter( pageResult -> LinksClassifier.isGoodLink(pageResult.httpStatusCode()))
                .map( PageResult::url)
                .collect(Collectors.joining("\n"));
    }
}
