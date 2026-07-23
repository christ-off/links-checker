package com.ptl.linkschecker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "links-checker")
public record LinksCheckerProperties(
        int requestTimeoutSeconds,
        int maxRequestAttempts,
        int maxConcurrentHosts,
        String userAgent,
        List<String> sitesToSkip
) {
    public LinksCheckerProperties {
        if (requestTimeoutSeconds <= 0) requestTimeoutSeconds = 30;
        if (maxRequestAttempts <= 0) maxRequestAttempts = 3;
        if (maxConcurrentHosts <= 0) maxConcurrentHosts = 8;
        if (userAgent == null || userAgent.isBlank())
            userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:143.0) Gecko/20100101 Firefox/143.0";
        if (sitesToSkip == null) sitesToSkip = List.of();
    }
}