package com.ptl.linkschecker.core;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.exceptions.LinksCrawlerException;
import com.ptl.linkschecker.utils.ProgressCounter;

import java.util.List;

public interface LinksCrawler {

    void processSite(String startUrl, ProgressCounter progressCounter) throws LinksCrawlerException;

    List<PageResult> getLinks();
}
