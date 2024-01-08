package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class LinkRetrieverImplTest {

    private final LinkRetriever tested = new LinkRetrieverImpl();
    @Test
    void should_gracefully_handle_missing_content(){
        // GIVEN
        PageResult error404 = new PageResult("", 404);
        // WHEN
        List<String> result = tested.retrieveBodyLinks(error404);
        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void should_handle_simple_content(){
        // GIVEN
        PageResult simple = new PageResult("<html><body><a href=\"https://example.com\">Link1</a></body></html>", 200);
        // WHEN
        List<String> result = tested.retrieveBodyLinks(simple);
        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1,result.size(), "Must retrieve one link");
        Assertions.assertEquals("https://example.com", result.getFirst());
    }

}