package com.ptl.linkschecker.domain;

import java.util.Optional;

public record PageResult(String url, Optional<String> content, int httpStatusCode) implements Comparable<PageResult> {

    @Override
    public int compareTo(PageResult o) {
        return url.compareTo(o.url);
    }
}
