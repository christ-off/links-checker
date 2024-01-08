package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.ProgressCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;

@ShellComponent
@RequiredArgsConstructor
public class CheckCommand {

    private final LinksCrawler linksCrawler;
    private final ProgressCounter progressCounter;

    @ShellMethod(key = "check", value = "Check the website given has an argument")
    public String check(
            @Option(defaultValue = "http://localhost:4000") String website
    ) throws IOException, InterruptedException {
        linksCrawler.processSite(website, progressCounter);
        return "Analyzed : " + website + " - Good " + linksCrawler.getAllGoodLinks().size() + " bad " + linksCrawler.getAllBadLinks().size();
    }
}
