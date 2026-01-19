package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
public class CheckCommand {

    private final LinksCrawler linksCrawler;
    private final ProgressCounter progressCounter;

    public CheckCommand(LinksCrawler linksCrawler, ProgressCounter progressCounter) {
        this.linksCrawler = linksCrawler;
        this.progressCounter = progressCounter;
    }

    @Command( name= "check", description = "Check the website given has an argument")
    public String check(
            @Option( longName = "website", description = "Website to crawl", defaultValue = "http://localhost:4000") String website
    ) throws InterruptedException {
        linksCrawler.processSite(website, progressCounter);
        return "Links : " + linksCrawler.getLinks().size();
    }
}