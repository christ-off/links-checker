package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;

import java.util.List;
import java.util.Optional;

public interface LinksManager {

    void reset();

    void addNewLinks(List<String> urls);
    String getNextUnProcessedLink();

    void updateLink(String url, Optional<String> content, int httpStatusCode);

    List<PageResult> getLinks();

}
