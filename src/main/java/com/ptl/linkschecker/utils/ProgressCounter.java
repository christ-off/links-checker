package com.ptl.linkschecker.utils;

public class ProgressCounter {

    private int count;

    private static final int MAX_COLS = 100;

    public void tick() {
        IO.print(".");
        count++;
        if (count % MAX_COLS == 0){
            IO.println(" - " + count);
        }
    }
}
