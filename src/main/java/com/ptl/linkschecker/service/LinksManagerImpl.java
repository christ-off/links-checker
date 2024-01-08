package com.ptl.linkschecker.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class LinksManagerImpl implements LinksManager {

    private Map<String, Integer> urlToStatusMap = new HashMap<>();

    @Override
    public void reset() {
        urlToStatusMap = new HashMap<>();
    }

    public void addNewLinks(List<String> urls){
        urls.forEach( url -> {
            if (!urlToStatusMap.containsKey(url)){
                urlToStatusMap.put(url,null);
            }
        });
    }
    public Optional<String> getNextUnProcessedLink(){
        return urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() == null )
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public void updateLink(String url, int httpStatusCode){
        urlToStatusMap.put(url,httpStatusCode);
    }

    private boolean isGoodLink(int httpStatus){
        return httpStatus >= 200 && httpStatus < 300;
    }
    public List<String> getAllGoodLinks(){
        return urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() != null && isGoodLink(url.getValue()) )
                .map(Map.Entry::getKey)
                .toList();
    }
    public List<String> getAllBadLinks(){
        return urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() != null && !isGoodLink(url.getValue()) )
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<String> getAllUntestedLinks(){
        return urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() != null && !isGoodLink(url.getValue()) )
                .map(Map.Entry::getKey)
                .toList();
    }

}
