package com.ptl.linkschecker.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageResultTest {

    @Test
    void compareTo_orders_by_url_ascending() {
        PageResult a = new PageResult("https://a.com", null, 200);
        PageResult b = new PageResult("https://b.com", null, 200);

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareTo_equal_urls_returns_zero() {
        PageResult a = new PageResult("https://a.com", "body", 200);
        PageResult b = new PageResult("https://a.com", null, 404);

        assertEquals(0, a.compareTo(b));
    }
}