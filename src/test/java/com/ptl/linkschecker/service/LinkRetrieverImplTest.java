package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class LinkRetrieverImplTest {

    private final LinkRetriever tested = new LinkRetriever();
    @Test
    void should_gracefully_handle_missing_content(){

        PageResult error404 = new PageResult("https://www.example.com", null,404);

        List<String> result = tested.retrieveBodyLinks(error404);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void should_handle_simple_content(){

        PageResult simple = new PageResult("https://www.example.com",
                "<html><body><a href=\"https://www.example.net\">Link1</a></body></html>",
                200);

        List<String> result = tested.retrieveBodyLinks(simple);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1,result.size(), "Must retrieve one link");
        Assertions.assertEquals("https://www.example.net", result.getFirst());
    }

    @Test
    void should_filter_out_self_reference_link(){

        PageResult simple = new PageResult("https://www.example.com",
                "<html><body><a href=\"#top\">Go To Top</a></body></html>",
                200);

        List<String> result = tested.retrieveBodyLinks(simple);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "Must not retrieve #links");
    }
   @Test
    void should_remove_fragment_identifier(){

        PageResult simple = new PageResult(
                "https://www.example.com",
                "<html><body><a href=\"/content#part1\">Go To Top</a></body></html>",
                200);

        List<String> result = tested.retrieveBodyLinks(simple);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1,result.size(), "Must retrieve one link");
        Assertions.assertEquals("https://www.example.com/content", result.getFirst());
    }

    @Test
    void should_resolve_relative_link_against_page_url(){

        PageResult subPage = new PageResult("https://www.example.com/posts/",
                "<html><body><a href=\"article.html\">Article</a></body></html>",
                200);

        List<String> result = tested.retrieveBodyLinks(subPage);

        Assertions.assertEquals(List.of("https://www.example.com/posts/article.html"), result);
    }

    @Test
    void should_ignore_non_http_links(){

        PageResult simple = new PageResult("https://www.example.com",
                "<html><body>"
                        + "<a href=\"mailto:someone@example.com\">Mail</a>"
                        + "<a href=\"javascript:void(0)\">JS</a>"
                        + "<a href=\"tel:+123456\">Call</a>"
                        + "</body></html>",
                200);

        List<String> result = tested.retrieveBodyLinks(simple);

        Assertions.assertTrue(result.isEmpty(), "Must only keep http(s) links");
    }

    @Test
    void should_be_safe_from_empty_links(){

        PageResult simple = new PageResult("https://www.example.com",
                "<html><body><a href=\"\">Go To Top</a></body></html>",
                200);

        List<String> result = tested.retrieveBodyLinks(simple);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "Must not retrieve #links");
    }

}
