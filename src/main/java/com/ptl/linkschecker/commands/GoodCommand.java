package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class GoodCommand {

    private final LinksCrawler linksCrawler;

    @ShellMethod(key = "good", value = "Get all good links")
    public String good() {
        return String.join("\n", linksCrawler.getAllGoodLinks());
    }
}
