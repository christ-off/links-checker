package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class LinksManager {

    private Map<String, PageResult> urlToStatusMap = new HashMap<>();

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
        Optional<Map.Entry<String, PageResult>> first = urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() == null )
                .findFirst();
        if (first.isPresent()) {
            updateLink(first.get().getKey(), null, LinksClassifier.BORROWED);
            return first.get().getKey();
        } else {
            return null;
        }
    }

    public void updateLink(String url, @Nullable String content, int httpStatusCode){
        urlToStatusMap.put(url, new PageResult(url, content, httpStatusCode));
    }


    public List<PageResult> getLinks(){
        return urlToStatusMap.values().stream().toList();
    }

}
