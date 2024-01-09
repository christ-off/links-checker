package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LinkRetrieverImpl implements  LinkRetriever {

    public List<String> retrieveBodyLinks(PageResult pageContent){
        if (pageContent.httpStatusCode() >= 200 && pageContent.httpStatusCode() < 300 ){
            Document doc = Jsoup.parse(pageContent.body());
            Elements links = doc.select("a");
            return links.eachAttr("href")
                    .stream()
                    .filter( link -> !link.startsWith("#"))
                    .map( link -> link.split("#", 2)[0])
                    .filter( link -> !link.trim().isEmpty())
                    .toList();
        }
        return new ArrayList<>(0);
    }
}
