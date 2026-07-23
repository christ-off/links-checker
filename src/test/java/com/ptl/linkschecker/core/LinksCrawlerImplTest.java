package com.ptl.linkschecker.core;

import com.ptl.linkschecker.config.LinksCheckerConfig;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.ProgressCounter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import java.util.Map;


@ExtendWith({SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(classes = { LinksCheckerConfig.class })
class LinksCrawlerImplTest {

    @Autowired
    LinksCrawler linksCrawler;

    private MockWebServer mockWebServer;

    private String startUrl;

    @Mock
    private ProgressCounter progressCounter;

    @BeforeEach
    void init() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.startUrl = this.mockWebServer.url("/").toString();
    }

    @AfterEach
    void shutdown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    void should_handle_regular_content() {
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body><a href=\"" + startUrl + "bad\">external</a></body><html>"));
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        linksCrawler.processSite(startUrl, progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(2, links.size());
    }

    @Test
    void should_handle_redirect() {
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(302).setHeader("Location", "https://www.example.net"));

        linksCrawler.processSite(startUrl, this.progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(1, links.size());
        Assertions.assertEquals("https://www.example.net", links.getFirst().content());
    }

    @Test
    void should_resolve_links_relative_to_current_page() {
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body><a href=\"/posts/\">posts</a></body></html>"));
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body><a href=\"article.html\">article</a></body></html>"));
        this.mockWebServer.enqueue(new MockResponse().setBody("the article"));

        linksCrawler.processSite(startUrl, progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(3, links.size());
        Assertions.assertTrue(links.stream().anyMatch(r -> r.url().endsWith("/posts/article.html")),
                "relative link must resolve against the page it was found on");
        Assertions.assertTrue(links.stream().allMatch(r -> r.httpStatusCode() == 200));
    }

    @Test
    void should_not_retain_page_bodies() {
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body>a large page body</body></html>"));

        linksCrawler.processSite(startUrl, progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(1, links.size());
        Assertions.assertNull(links.getFirst().content(), "bodies of non-redirect pages must not be retained");
    }

    @Test
    void should_fetch_internal_and_external_hosts_in_the_same_wave() throws IOException {
        MockWebServer externalServer = new MockWebServer();
        externalServer.enqueue(new MockResponse().setBody("external ok"));
        externalServer.start();
        // 127.0.0.1 instead of localhost: a distinct host, so it lands in its own per-host fetch group
        String externalUrl = "http://127.0.0.1:" + externalServer.getPort() + "/external";

        // start page links to an internal page and an external host: both are crawled in the same wave, in two parallel host groups
        this.mockWebServer.enqueue(new MockResponse().setBody(
                "<html><body><a href=\"" + externalUrl + "\">ext</a><a href=\"/page\">internal</a></body></html>"));
        this.mockWebServer.enqueue(new MockResponse().setBody("internal page"));

        linksCrawler.processSite(startUrl, progressCounter);
        List<PageResult> links = linksCrawler.getLinks();

        Assertions.assertEquals(3, links.size());
        Assertions.assertTrue(links.stream().anyMatch(r -> r.url().equals(externalUrl)));
        Assertions.assertTrue(links.stream().allMatch(r -> r.httpStatusCode() == 200));

        externalServer.shutdown();
    }

    @Test
    void should_reject_non_http_scheme() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> linksCrawler.processSite("file:///etc/passwd", progressCounter));
    }

    @Test
    void should_cap_concurrent_host_fan_out_per_wave() {
        LinksCrawler cappedCrawler = new LinksCrawler(
                new com.ptl.linkschecker.service.ContentRetriever(
                        java.net.http.HttpClient.newHttpClient(), java.time.Duration.ofSeconds(1), 1),
                new com.ptl.linkschecker.service.LinkRetriever(),
                new com.ptl.linkschecker.service.LinksManager(),
                new com.ptl.linkschecker.config.SkippedSites(List.of()),
                1);

        StringBuilder body = new StringBuilder("<html><body>");
        for (int i = 0; i < 3; i++) {
            body.append("<a href=\"http://127.0.0.").append(i + 1).append(":1/x\">l").append(i).append("</a>");
        }
        body.append("</body></html>");
        this.mockWebServer.enqueue(new MockResponse().setBody(body.toString()));

        // unreachable hosts (nothing listens on port 1): just verifying the wave doesn't hang or error
        // when host groups outnumber maxConcurrentHosts, each chunk is processed before the next starts
        cappedCrawler.processSite(startUrl, progressCounter);
        List<PageResult> links = cappedCrawler.getLinks();

        Assertions.assertEquals(4, links.size());
    }

    @Test
    void should_count_queries_per_host() {
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body><a href=\"/page\">link</a></body></html>"));
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("page"));

        linksCrawler.processSite(startUrl, progressCounter);
        Map<String, Long> queriesPerHost = linksCrawler.getQueriesPerHost();

        Assertions.assertFalse(queriesPerHost.isEmpty());
    }
}
