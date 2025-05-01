package com.ptl.linkschecker.core;

import com.ptl.linkschecker.config.LinksCheckerConfig;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.ProgressCounter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { LinksCheckerConfig.class })
class LinksCrawlerImplTest {

    @Autowired
    LinksCrawler linksCrawler;

    private MockWebServer mockWebServer;

    @Mock
    private ProgressCounter progressCounter;

    @BeforeEach
    void init() {
        this.mockWebServer = new MockWebServer();
    }

    @Test
    void should_handle_regular_content() throws IOException, InterruptedException {
        this.mockWebServer.start();
        String startUrl = this.mockWebServer.url("/").toString();
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body><a href=\"" + startUrl+ "bad\">external</a></body><html>"));
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        linksCrawler.processSite(startUrl,progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(2,links.size());
        // AND
        this.mockWebServer.shutdown();
    }

    @Test
    void should_handle_redirect() throws IOException, InterruptedException {

        this.mockWebServer.start();
        String startUrl = this.mockWebServer.url("/").toString();
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(302).setHeader("Location", "https://www.example.net"));

        linksCrawler.processSite(startUrl,progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(1,links.size());
        Assertions.assertEquals("https://www.example.net", links.getFirst().content());

        this.mockWebServer.shutdown();
    }
}