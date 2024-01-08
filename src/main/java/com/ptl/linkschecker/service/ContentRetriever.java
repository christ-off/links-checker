package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;

import java.io.IOException;

public interface ContentRetriever {

    PageResult retrievePageContent(String url) throws IOException, InterruptedException;
}
