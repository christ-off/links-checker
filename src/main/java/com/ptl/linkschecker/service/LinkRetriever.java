package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;

import java.util.List;

public interface LinkRetriever {

    List<String> retrieveBodyLinks(PageResult pageContent);
}
