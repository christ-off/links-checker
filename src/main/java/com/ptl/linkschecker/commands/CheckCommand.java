package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;


@ShellComponent
public class CheckCommand {

    private final LinksCrawler linksCrawler;
    private final ProgressCounter progressCounter;

    public CheckCommand(LinksCrawler linksCrawler, ProgressCounter progressCounter) {
        this.linksCrawler = linksCrawler;
        this.progressCounter = progressCounter;
    }

    @ShellMethod(key = "check", value = "Check the website given has an argument")
    public String check(
            @Option(defaultValue = "http://localhost:4000") String website
    ) throws InterruptedException {
        linksCrawler.processSite(website, progressCounter);
        return "Links : " + linksCrawler.getLinks().size();
    }
}
