package com.ptl.linkschecker.domain;

import org.springframework.lang.Nullable;

public record PageResult(String url, @Nullable String content, int httpStatusCode) implements Comparable<PageResult> {

    @Override
    public int compareTo(PageResult o) {
        return url.compareTo(o.url);
    }
}
