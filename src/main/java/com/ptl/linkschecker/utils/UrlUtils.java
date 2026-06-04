package com.ptl.linkschecker.utils;

import java.net.URI;

public class UrlUtils {

    private UrlUtils() {
    }

    public static void validateScheme(String url) {
        String scheme = URI.create(url).getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("Only HTTP and HTTPS URLs are allowed (SSRF protection)");
        }
    }

    public static String extractHost(String url) {
        try {
            String host = URI.create(url).getHost();
            return host != null ? host : url;
        } catch (IllegalArgumentException _) {
            return url;
        }
    }
}
