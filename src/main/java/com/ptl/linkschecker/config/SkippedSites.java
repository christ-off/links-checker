package com.ptl.linkschecker.config;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SkippedSites {

    private final Set<String> skippedHostnames = ConcurrentHashMap.newKeySet();

    public SkippedSites(List<String> sitesToSkip) {
        addSites(sitesToSkip);
    }

    public void setSitesToSkip(List<String> sitesToSkip) {
        skippedHostnames.clear();
        addSites(sitesToSkip);
    }

    private void addSites(List<String> sitesToSkip) {
        if (sitesToSkip == null) return;
        for (String site : sitesToSkip) {
            if (site == null || site.isBlank()) continue;
            try {
                String host = URI.create(site.trim()).getHost();
                if (host != null) {
                    skippedHostnames.add(host.toLowerCase(java.util.Locale.ROOT));
                }
            }
            catch (IllegalArgumentException _) {
                // unparseable configured site: nothing to skip
            }
        }
    }

    public boolean isSkipped(String url) {
        try {
            String host = URI.create(url).getHost();
            return host != null && skippedHostnames.contains(host.toLowerCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException _) {
            return false;
        }
    }
}
