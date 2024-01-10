package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class LinksManagerImpl implements LinksManager {

    private static final Integer BORROWED = 0;

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
    @Nullable
    public String getNextUnProcessedLink(){
        var first = urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() == null )
                .findFirst();
        if (first.isPresent()) {
            first.get().setValue(BORROWED);
            return first.get().getKey();
        } else {
            return null;
        }
    }

    public void updateLink(String url, int httpStatusCode){
        urlToStatusMap.put(url,httpStatusCode);
    }

    private boolean isGoodLink(int httpStatus){
        return httpStatus >= 200 && httpStatus < 300;
    }
    public List<PageResult> getAllGoodLinks(){
        return urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() != null && isGoodLink(url.getValue()) )
                .map( entry -> new PageResult(entry.getKey(), entry.getValue()))
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

    public long countUnprocessed(){
        return urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() == null || BORROWED.equals(url.getValue()) )
                .count();
    }

}
