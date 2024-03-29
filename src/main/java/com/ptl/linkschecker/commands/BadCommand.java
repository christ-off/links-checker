package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.LinksClassifier;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class BadCommand {

    private final LinksCrawler linksCrawler;

    @ShellMethod(key = "bad", value = "Get all bad links")
    public String bad() {
        return linksCrawler
                .getLinks()
                .stream()
                .filter( pageResult -> LinksClassifier.isBadLink(pageResult.httpStatusCode()))
                .map( pageResult -> pageResult.url() + " -> " + pageResult.httpStatusCode())
                .collect(Collectors.joining("\n"));
    }
}
