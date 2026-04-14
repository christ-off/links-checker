package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

class ContentRetrieverImplTest {

    ContentRetriever tested;

    private MockWebServer mockWebServer;

    @BeforeEach
    void init() {
        this.mockWebServer = new MockWebServer();
        this.tested = new ContentRetriever(HttpClient.newHttpClient(), Duration.ofMillis(200));
    }

    @Test
    void should_retrieve_available_content() throws IOException, InterruptedException {

        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body>hello, world!</body><html>"));
        this.mockWebServer.start();

        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());

        Assertions.assertEquals(200,result.httpStatusCode());
        Assertions.assertNotNull(result.content());
        Assertions.assertEquals("<html><body>hello, world!</body><html>",result.content());

        this.mockWebServer.shutdown();
    }

    @Test
    void should_manage_missing_content() throws IOException, InterruptedException {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        this.mockWebServer.start();

        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());

        Assertions.assertEquals(404,result.httpStatusCode());
        Assertions.assertTrue(result.content() == null || result.content().isEmpty());

        this.mockWebServer.shutdown();
    }

    @Test
    void should_extract_location_header_on_redirect() throws IOException, InterruptedException {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", "https://new.example.com/page"));
        this.mockWebServer.start();

        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());

        Assertions.assertEquals(301, result.httpStatusCode());
        Assertions.assertEquals("https://new.example.com/page", result.content());

        this.mockWebServer.shutdown();
    }

    @Test
    void should_return_500_on_invalid_url() throws InterruptedException {
        PageResult result = tested.retrievePageContent("not-a-valid-url");

        Assertions.assertEquals(500, result.httpStatusCode());
    }

    @Test
    void should_return_408_on_timeout() throws IOException, InterruptedException {

        this.mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
                Thread.sleep(500);
                return new MockResponse().setBody("too late");
            }
        });
        this.mockWebServer.start();

        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());

        Assertions.assertEquals(408, result.httpStatusCode());

        this.mockWebServer.shutdown();
    }
}
