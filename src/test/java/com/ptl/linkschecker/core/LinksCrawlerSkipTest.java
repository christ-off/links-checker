package com.ptl.linkschecker.core;

import com.ptl.linkschecker.config.LinksCheckerConfig;
import com.ptl.linkschecker.config.SkippedSites;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.ProgressCounter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {LinksCheckerConfig.class})
class LinksCrawlerSkipTest {

    @Autowired
    LinksCrawler linksCrawler;

    @Autowired
    SkippedSites skippedSites;

    @Mock
    private ProgressCounter progressCounter;

    private MockWebServer mockWebServer;

    @BeforeEach
    void init() {
        this.mockWebServer = new MockWebServer();
    }

    @AfterEach
    void shutdown() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void should_skip_configured_site() throws IOException {
        skippedSites.setSitesToSkip(List.of("https://www.babelio.com"));

        mockWebServer.start();
        String startUrl = mockWebServer.url("/").toString();
        String babelioLink = "https://www.babelio.com/skip-me";

        // Enqueue responses: start page (with babelio + internal links), internal page content
        this.mockWebServer.enqueue(
                new MockResponse().setBody(
                        "<html><body>"
                        + "<a href=\"" + babelioLink + "\">babelio</a>"
                        + "<a href=\"/internal\">internal</a>"
                        + "</body></html>"));
        this.mockWebServer.enqueue(new MockResponse().setBody("internal page"));

        linksCrawler.processSite(startUrl, progressCounter);

        List<PageResult> links = linksCrawler.getLinks();
        // Only startUrl and /internal page — babelio is skipped
        assertEquals(2, links.size());

        // Verify skipped tick was called exactly once
        verify(progressCounter, times(1)).tick(anyInt(), anyBoolean(), eq(true));
    }

    @Test
    void should_not_skip_when_no_sites_configured() throws IOException {
        skippedSites.setSitesToSkip(List.of());

        mockWebServer.start();
        String startUrl = mockWebServer.url("/").toString();

        this.mockWebServer.enqueue(
                new MockResponse().setBody(
                        "<html><body><a href=\"/page\">link</a></body></html>"));
        this.mockWebServer.enqueue(new MockResponse().setBody("page content"));

        linksCrawler.processSite(startUrl, progressCounter);

        List<PageResult> links = linksCrawler.getLinks();
        assertEquals(2, links.size());

        // No skipped ticks
        verify(progressCounter, never()).tick(anyInt(), anyBoolean(), eq(true));
    }
}
