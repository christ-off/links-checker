package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class LinksManagerImpl implements LinksManager {

    private Map<String, PageResult> urlToStatusMap = new HashMap<>();

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
        Optional<Map.Entry<String, PageResult>> first = urlToStatusMap.entrySet().stream()
                .filter(url -> url.getValue() == null )
                .findFirst();
        if (first.isPresent()) {
            updateLink(first.get().getKey(), Optional.empty(), LinksClassifier.BORROWED);
            return first.get().getKey();
        } else {
            return null;
        }
    }

    public void updateLink(String url, Optional<String> content, int httpStatusCode){
        urlToStatusMap.put(url, new PageResult(url,content,httpStatusCode));
    }


    public List<PageResult> getLinks(){
        return urlToStatusMap.values().stream().toList();
    }

}
