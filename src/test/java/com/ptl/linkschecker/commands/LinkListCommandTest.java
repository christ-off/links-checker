package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.domain.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkListCommandTest {

    LinkListCommand tested = new LinkListCommand();

    @Test
    void should_list_bad_links() {
        List<PageResult> links = List.of(
                new PageResult("https://ok.com", "ok", 200),
                new PageResult("https://bad.com", null, 404),
                new PageResult("https://error.com", null, 500)
        );

        String result = tested.badLinks(links);

        assertTrue(result.contains("https://bad.com"));
        assertTrue(result.contains("404"));
        assertTrue(result.contains("https://error.com"));
        assertTrue(result.contains("500"));
        assertFalse(result.contains("https://ok.com"));
    }

    @Test
    void should_return_empty_string_when_no_bad_links() {
        List<PageResult> links = List.of(new PageResult("https://ok.com", "ok", 200));

        assertEquals("", tested.badLinks(links));
    }

    @Test
    void should_list_moved_links_with_their_target() {
        List<PageResult> links = List.of(
                new PageResult("https://ok.com", "ok", 200),
                new PageResult("https://moved.com", "https://new.com", 301),
                new PageResult("https://temp.com", null, 302)
        );

        String result = tested.movedLinks(links);

        assertTrue(result.contains("https://moved.com -> https://new.com"));
        assertTrue(result.contains("https://temp.com"));
        assertFalse(result.contains("https://temp.com -> "));
        assertFalse(result.contains("https://ok.com"));
    }

    @Test
    void should_return_empty_string_when_no_redirects() {
        List<PageResult> links = List.of(new PageResult("https://ok.com", "ok", 200));

        assertEquals("", tested.movedLinks(links));
    }

    @Test
    void should_list_internal_links_with_percent() {
        List<PageResult> links = List.of(
                new PageResult("https://site.com/a%20b", "ok", 200),
                new PageResult("https://site.com/plain", "ok", 200),
                new PageResult("https://other.com/x%20y", "ok", 200)
        );

        String result = tested.internalLinksWithPercent(links, "https://site.com");

        assertTrue(result.contains("https://site.com/a%20b"));
        assertFalse(result.contains("https://site.com/plain"));
        assertFalse(result.contains("https://other.com/x%20y"));
    }

    @Test
    void should_return_empty_string_when_no_internal_percent_links() {
        List<PageResult> links = List.of(new PageResult("https://site.com/plain", "ok", 200));

        assertEquals("", tested.internalLinksWithPercent(links, "https://site.com"));
    }
}
