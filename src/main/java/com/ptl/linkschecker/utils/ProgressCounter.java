package com.ptl.linkschecker.utils;

import org.jline.terminal.Terminal;

// https://medium.com/agency04/developing-cli-application-with-spring-shell-part-3-b4c247fdf558
public class ProgressCounter {

    private final Terminal terminal;

    private int count;

    private static final int MAX_COLS = 100;

    public ProgressCounter(Terminal terminal) {
        this.terminal = terminal;
    }

    public void tick() {
        terminal.writer().print(".");
        count++;
        if (count % MAX_COLS == 0){
            terminal.writer().println(" - " + count);
        }
        terminal.flush();
    }
}
