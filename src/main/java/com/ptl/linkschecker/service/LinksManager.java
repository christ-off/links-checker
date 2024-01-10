package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;

import java.util.List;

public interface LinksManager {

    void reset();

    void addNewLinks(List<String> urls);
    String getNextUnProcessedLink();

    void updateLink(String url, int httpStatusCode);

    List<PageResult> getAllGoodLinks();
    List<String> getAllBadLinks();
    List<String> getAllUntestedLinks();

    long countUnprocessed();

}
