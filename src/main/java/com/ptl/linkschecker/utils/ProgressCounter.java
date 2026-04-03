package com.ptl.linkschecker.utils;

public class ProgressCounter {

    private int count;

    private static final int MAX_COLS = 100;

    public void tick(int httpStatusCode, boolean internal) {
        String symbol;
        if (internal) {
            symbol = "S";
        } else {
            if (httpStatusCode >= 500) symbol = "X";
            else symbol = httpStatusCode >= 400 ? "x" : ".";
        }
        IO.print(symbol);
        count++;
        if (count % MAX_COLS == 0){
            IO.println(" - " + count);
        }
    }

    public void printHostStats(java.util.Map<String, Long> queriesPerHost) {
        IO.println("\n--- QUERIES PER HOST ---");
        queriesPerHost.forEach((host, queries) -> IO.println(host + ": " + queries));
    }
}
