package com.ptl.linkschecker.commands;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckCommandTest {

    @Mock
    LinksCrawler linksCrawler;

    @Mock
    ProgressCounter progressCounter;

    @InjectMocks
    CheckCommand tested;

    @Test
    void should_process_site_and_return_link_count() {
        when(linksCrawler.getLinks()).thenReturn(List.of(
                new PageResult("https://ok.com", "ok", 200),
                new PageResult("https://other.com", "other", 200)
        ));

        String result = tested.check("https://example.com");

        verify(linksCrawler).processSite("https://example.com", progressCounter);
        assertEquals("Links : 2", result);
    }
}