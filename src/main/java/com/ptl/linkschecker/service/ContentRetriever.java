package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;

public interface ContentRetriever {

    PageResult retrievePageContent(String url) throws InterruptedException;
}
