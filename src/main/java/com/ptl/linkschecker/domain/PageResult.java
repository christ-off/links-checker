package com.ptl.linkschecker.domain;

public record PageResult(String content, int httpStatusCode) implements Comparable<PageResult> {

    @Override
    public int compareTo(PageResult o) {
        return this.content().compareTo(o.content());
    }
}
