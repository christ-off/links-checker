package com.ptl.linkschecker.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinksCheckerPropertiesTest {

    @Test
    void should_use_defaults_when_values_are_negative_or_blank() {
        LinksCheckerProperties props = new LinksCheckerProperties(-1, 0, -5, "", null);

        assertEquals(30, props.requestTimeoutSeconds());
        assertEquals(3, props.maxRequestAttempts());
        assertEquals(8, props.maxConcurrentHosts());
        assertEquals("Mozilla/5.0 (X11; Linux x86_64; rv:143.0) Gecko/20100101 Firefox/143.0", props.userAgent());
        assertTrue(props.sitesToSkip().isEmpty());
    }

    @Test
    void should_accept_valid_values() {
        LinksCheckerProperties props = new LinksCheckerProperties(10, 5, 3, "MyBot/1.0", List.of("spam.com"));

        assertEquals(10, props.requestTimeoutSeconds());
        assertEquals(5, props.maxRequestAttempts());
        assertEquals(3, props.maxConcurrentHosts());
        assertEquals("MyBot/1.0", props.userAgent());
        assertEquals(List.of("spam.com"), props.sitesToSkip());
    }

    @Test
    void should_accept_empty_string_as_blank_user_agent() {
        LinksCheckerProperties props = new LinksCheckerProperties(30, 3, 8, "  ", List.of());

        assertEquals("Mozilla/5.0 (X11; Linux x86_64; rv:143.0) Gecko/20100101 Firefox/143.0", props.userAgent());
    }
}
