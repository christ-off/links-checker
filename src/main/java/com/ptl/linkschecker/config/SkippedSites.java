package com.ptl.linkschecker.config;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SkippedSites {

    private final Set<String> skippedHostnames = ConcurrentHashMap.newKeySet();

    public SkippedSites(List<String> sitesToSkip) {
        if (sitesToSkip != null) {
            for (String site : sitesToSkip) {
                addSite(site);
            }
        }
    }

    private void addSite(String site) {
        if (site == null || site.isBlank()) return;
        try {
            String host = URI.create(site.trim()).getHost();
            if (host != null) {
                skippedHostnames.add(host.toLowerCase(java.util.Locale.ROOT));
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void setSitesToSkip(List<String> sitesToSkip) {
        skippedHostnames.clear();
        if (sitesToSkip != null) {
            sitesToSkip.forEach(this::addSite);
        }
    }

    public boolean isSkipped(String url) {
        try {
            String host = URI.create(url).getHost();
            return host != null && skippedHostnames.contains(host.toLowerCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}