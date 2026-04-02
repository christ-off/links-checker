package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.stereotype.Component;

@Component
public class CheckCommand {

    private final LinksCrawler linksCrawler;
    private final ProgressCounter progressCounter;

    public CheckCommand(LinksCrawler linksCrawler, ProgressCounter progressCounter) {
        this.linksCrawler = linksCrawler;
        this.progressCounter = progressCounter;
    }

    public String check(String website) {
        linksCrawler.processSite(website, progressCounter);
        return "Links : " + linksCrawler.getLinks().size();
    }
}