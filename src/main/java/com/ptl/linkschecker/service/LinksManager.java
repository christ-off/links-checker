package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinksManager {

    private static final int BORROWED_STATUS = 0;
    private static final PageResult BORROWED_SENTINEL = new PageResult("", null, BORROWED_STATUS);

    private final Map<String, PageResult> urlToStatusMap = new HashMap<>();
    private final Deque<String> pendingQueue = new ArrayDeque<>();

    public void reset() {
        urlToStatusMap.clear();
        pendingQueue.clear();
    }

    public void addNewLinks(List<String> urls) {
        for (String url : urls) {
            if (urlToStatusMap.putIfAbsent(url, BORROWED_SENTINEL) == null) {
                pendingQueue.add(url);
            }
        }
    }

    @Nullable
    public String getNextUnProcessedLink() {
        return pendingQueue.poll();
    }

    public void updateLink(String url, @Nullable String content, int httpStatusCode) {
        urlToStatusMap.put(url, new PageResult(url, content, httpStatusCode));
    }

    public List<PageResult> getLinks() {
        return urlToStatusMap.values().stream()
                .filter(r -> r != BORROWED_SENTINEL)
                .toList();
    }
}
