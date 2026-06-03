package com.ptl.linkschecker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "links-checker")
public record LinksCheckerProperties(
        int requestTimeoutSeconds,
        List<String> sitesToSkip
) {
    public LinksCheckerProperties {
        if (requestTimeoutSeconds <= 0) requestTimeoutSeconds = 30;
        if (sitesToSkip == null) sitesToSkip = List.of();
    }
}