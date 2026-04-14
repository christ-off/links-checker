package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.domain.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovedCommandTest {

    @Mock
    LinksCrawler linksCrawler;

    @InjectMocks
    MovedCommand tested;

    @Test
    void should_list_moved_links() {
        when(linksCrawler.getLinks()).thenReturn(List.of(
                new PageResult("https://ok.com", "ok", 200),
                new PageResult("https://moved.com", "https://new.com", 301),
                new PageResult("https://temp.com", "https://other.com", 302)
        ));

        String result = tested.good();

        assertTrue(result.contains("https://moved.com"));
        assertTrue(result.contains("https://temp.com"));
        assertFalse(result.contains("https://ok.com"));
    }

    @Test
    void should_return_empty_string_when_no_redirects() {
        when(linksCrawler.getLinks()).thenReturn(List.of(
                new PageResult("https://ok.com", "ok", 200)
        ));

        assertEquals("", tested.good());
    }
}