package com.ptl.linkschecker.utils;

public class ProgressCounter {

    private int count = 0;

    private static final int MAX_COLS = 100;

    public void tick(int httpStatusCode, boolean internal, boolean skipped) {
        String symbol;
        if (internal) {
            symbol = "I";
        } else if (skipped) {
            symbol = "s";
        } else {
            if (httpStatusCode >= 500) symbol = "5";
            else symbol = httpStatusCode >= 400 ? "4" : ".";
        }
        IO.print(symbol);
        int n = ++count;
        if (n % MAX_COLS == 0) {
            IO.println(" - " + n);
        }
    }

    public void printHostStats(java.util.Map<String, Long> queriesPerHost) {
        IO.println("\n--- Queries per external host ---");
        queriesPerHost.entrySet().stream()
            .filter(e -> !e.getKey().contains("localhost"))
            .sorted(java.util.Map.Entry.comparingByValue(java.util.Comparator.reverseOrder()))
            .limit(20)
            .forEach(e -> IO.println(e.getKey() + ": " + e.getValue()));
    }
}
