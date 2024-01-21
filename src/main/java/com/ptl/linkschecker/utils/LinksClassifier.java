package com.ptl.linkschecker.utils;


public class LinksClassifier {

    private LinksClassifier(){

    }

    public static final Integer BORROWED = 0;

    public static boolean isGoodLink(int httpStatus){
        return httpStatus >= 200 && httpStatus < 300;
    }

    public static boolean isRedirectLink(int httpStatus){
        return httpStatus >= 300 && httpStatus < 400;
    }
    public static boolean isBadLink(int httpStatus){
        return httpStatus >= 400 ;
    }

    public static boolean isUntestedLink(int httpStatus){
        return httpStatus == BORROWED ;
    }
}
