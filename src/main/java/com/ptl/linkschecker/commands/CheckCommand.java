package com.ptl.linkschecker.commands;

import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class CheckCommand {

    @ShellMethod(key = "check", value = "Check the website given has an argument")
    public String check(
            @Option(defaultValue = "http://localhost:4000") String website
    ) {
        return "Hello world " + website;
    }
}
