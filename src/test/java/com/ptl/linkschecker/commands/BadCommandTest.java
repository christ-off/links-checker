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
class BadCommandTest {

    @Mock
    LinksCrawler linksCrawler;

    @InjectMocks
    BadCommand tested;

    @Test
    void should_list_bad_links() {
        when(linksCrawler.getLinks()).thenReturn(List.of(
                new PageResult("https://ok.com", "ok", 200),
                new PageResult("https://bad.com", null, 404),
                new PageResult("https://error.com", null, 500)
        ));

        String result = tested.bad();

        assertTrue(result.contains("https://bad.com"));
        assertTrue(result.contains("404"));
        assertTrue(result.contains("https://error.com"));
        assertTrue(result.contains("500"));
        assertFalse(result.contains("https://ok.com"));
    }

    @Test
    void should_return_empty_string_when_no_bad_links() {
        when(linksCrawler.getLinks()).thenReturn(List.of(
                new PageResult("https://ok.com", "ok", 200)
        ));

        assertEquals("", tested.bad());
    }
}