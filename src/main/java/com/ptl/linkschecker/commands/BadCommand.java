package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class BadCommand {

    private final LinksCrawler linksCrawler;

    @ShellMethod(key = "bad", value = "Get all bad links")
    public String bad() {
        return String.join("\n", linksCrawler.getAllBadLinks());
    }
}
