package com.ptl.linkschecker.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkippedSitesTest {

    @Test
    void should_skip_known_host() {
        SkippedSites sites = new SkippedSites(List.of("https://example.com"));
        assertTrue(sites.isSkipped("https://example.com/page"));
    }

    @Test
    void should_accept_unknown_host() {
        SkippedSites sites = new SkippedSites(List.of("https://example.com"));
        assertFalse(sites.isSkipped("https://other.com/resource"));
    }

    @Test
    void should_ignore_null_entries_in_list() {
        SkippedSites sites = new SkippedSites(Arrays.asList(null, "https://example.com", null));
        assertTrue(sites.isSkipped("https://example.com/page"));
    }

    @Test
    void should_ignore_blank_entries_in_list() {
        SkippedSites sites = new SkippedSites(List.of("", "  ", "https://example.com"));
        assertTrue(sites.isSkipped("https://example.com/page"));
    }

    @Test
    void should_ignore_unparseable_entries() {
        SkippedSites sites = new SkippedSites(List.of("not-a-valid-url", "https://example.com"));
        assertTrue(sites.isSkipped("https://example.com/page"));
    }

    @Test
    void should_lowercase_hostname_for_comparison() {
        SkippedSites sites = new SkippedSites(List.of("https://EXAMPLE.COM"));
        assertTrue(sites.isSkipped("https://example.com"));
    }

    @Test
    void should_handle_null_list() {
        SkippedSites sites = new SkippedSites(null);
        assertFalse(sites.isSkipped("https://anything.com"));
    }

    @Test
    void should_handle_invalid_url_in_is_skipped() {
        SkippedSites sites = new SkippedSites(List.of("https://example.com"));
        // Invalid URL should not throw, just return false
        assertFalse(sites.isSkipped("not a valid url"));
    }

    @Test
    void should_clear_sites_on_setSitesToSkip() {
        SkippedSites sites = new SkippedSites(List.of("https://example.com"));
        assertTrue(sites.isSkipped("https://example.com/page"));

        sites.setSitesToSkip(List.of("https://other.com"));
        assertFalse(sites.isSkipped("https://example.com/page"));
        assertTrue(sites.isSkipped("https://other.com/page"));
    }
}
