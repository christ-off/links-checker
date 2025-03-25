package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.net.http.HttpClient;

class ContentRetrieverImplTest {

    ContentRetriever tested;

    private MockWebServer mockWebServer;

    @BeforeEach
    void init() {
        this.mockWebServer = new MockWebServer();
        this.tested = new ContentRetriever(HttpClient.newHttpClient());
    }

    @Test
    void should_retrieve_available_content() throws IOException, InterruptedException {

        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body>hello, world!</body><html>"));
        this.mockWebServer.start();

        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());

        Assertions.assertEquals(200,result.httpStatusCode());
        Assertions.assertTrue(result.content().isPresent());
        Assertions.assertEquals("<html><body>hello, world!</body><html>",result.content().get());

        this.mockWebServer.shutdown();
    }

    @Test
    void should_manage_missing_content() throws IOException, InterruptedException {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        this.mockWebServer.start();

        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());

        Assertions.assertEquals(404,result.httpStatusCode());
        Assertions.assertTrue(result.content().isEmpty() || result.content().get().isEmpty());

        this.mockWebServer.shutdown();
    }
}
