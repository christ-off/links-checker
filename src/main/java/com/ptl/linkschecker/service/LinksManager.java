package com.ptl.linkschecker.service;

import java.util.List;
import java.util.Optional;

public interface LinksManager {

    void reset();

    void addNewLinks(List<String> urls);
    Optional<String> getNextUnProcessedLink();

    void updateLink(String url, int httpStatusCode);

    List<String> getAllGoodLinks();
    List<String> getAllBadLinks();
    List<String> getAllUntestedLinks();

}
