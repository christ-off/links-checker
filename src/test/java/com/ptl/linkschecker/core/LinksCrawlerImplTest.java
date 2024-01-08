package com.ptl.linkschecker.core;

import com.ptl.linkschecker.config.LinksCheckerConfig;
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
    void should_work() throws IOException, InterruptedException {
        // GIVEN
        this.mockWebServer.start();
        String startUrl = this.mockWebServer.url("/").toString();
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body><a href=\"" + startUrl+ "bad\">external</a></body><html>"));
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        // WHEN
        linksCrawler.processSite(startUrl,progressCounter);
        List<String> bad = linksCrawler.getAllBadLinks();
        // THEN
        Assertions.assertEquals(1,bad.size());
        Assertions.assertEquals(startUrl+"bad", bad.getFirst());
        // AND
        this.mockWebServer.shutdown();
    }
}