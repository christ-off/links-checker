package com.ptl.linkschecker.utils;

import org.jline.terminal.Terminal;

// https://medium.com/agency04/developing-cli-application-with-spring-shell-part-3-b4c247fdf558
public class ProgressCounterImpl implements ProgressCounter {

    private static final String CUU = "\u001B[A";

    private final Terminal terminal;
    private char[] spinner = {'|', '/', '-', '\\'};

    private String pattern = " %s: %d ";

    private int spinCounter = 0;
    boolean started;

    public ProgressCounterImpl(Terminal terminal) {
        this(terminal, null);
    }

    public ProgressCounterImpl(Terminal terminal, String pattern) {
        this(terminal, pattern, null);
    }

    public ProgressCounterImpl(Terminal terminal, String pattern, char[] spinner) {
        this.terminal = terminal;

        if (pattern != null) {
            this.pattern = pattern;
        }
        if (spinner != null) {
            this.spinner = spinner;
        }
    }

    public void display(int count, String message) {
        if (!started) {
            terminal.writer().println();
            started = true;
        }
        String progress = String.format(pattern, message, count);

        terminal.writer().println(CUU + "\r" + getSpinnerChar() + progress);
        terminal.flush();
    }

    public void display() {
        if (!started) {
            terminal.writer().println();
            started = true;
        }
        terminal.writer().println(CUU + "\r" + getSpinnerChar());
        terminal.flush();
    }

    public void reset() {
        spinCounter = 0;
        started = false;
    }

    private char getSpinnerChar() {
        char spinChar = spinner[spinCounter];
        spinCounter++;
        if (spinCounter == spinner.length) {
            spinCounter = 0;
        }
        return spinChar;
    }

}