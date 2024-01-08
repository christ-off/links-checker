package com.ptl.linkschecker.core;

import com.ptl.linkschecker.utils.ProgressCounter;

import java.io.IOException;
import java.util.List;

public interface LinksCrawler {

    void processSite(String startUrl, ProgressCounter progressCounter) throws IOException, InterruptedException;

    List<String> getAllBadLinks();
    List<String> getAllGoodLinks();
}
