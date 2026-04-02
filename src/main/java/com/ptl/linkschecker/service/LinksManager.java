package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class LinksManager {

    private static final PageResult BORROWED = new PageResult("", null, LinksClassifier.BORROWED);

    private final ConcurrentHashMap<String, PageResult> urlToStatusMap = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<String> pendingQueue = new LinkedBlockingQueue<>();

    public void reset() {
        urlToStatusMap.clear();
        pendingQueue.clear();
    }

    public void addNewLinks(List<String> urls) {
        urls.forEach(url -> {
            if (urlToStatusMap.putIfAbsent(url, BORROWED) == null) {
                pendingQueue.add(url);
            }
        });
    }

    /**
     * Atomically marks the url as in-progress if not already seen.
     * Returns true if the url was newly added (caller should process it).
     */
    public boolean tryAdd(String url) {
        return urlToStatusMap.putIfAbsent(url, BORROWED) == null;
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
                .filter(r -> r != BORROWED)
                .toList();
    }
}