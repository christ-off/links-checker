package com.ptl.linkschecker.utils;

public class LinksClassifier {

    private LinksClassifier() {
    }

    public static boolean isRedirectLink(int httpStatus) {
        return httpStatus >= 300 && httpStatus < 400;
    }

    public static boolean isBadLink(int httpStatus) {
        return httpStatus >= 400;
    }
}
