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
        this.tested = new ContentRetrieverImpl(HttpClient.newHttpClient());
    }

    @Test
    void should_retrieve_available_content() throws IOException, InterruptedException {
        // GIVEN
        this.mockWebServer.enqueue(new MockResponse().setBody("<html><body>hello, world!</body><html>"));
        this.mockWebServer.start();
        // WHEN
        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());
        // THEN
        Assertions.assertEquals(200,result.httpStatusCode());
        Assertions.assertEquals("<html><body>hello, world!</body><html>",result.body());
        // AND
        this.mockWebServer.shutdown();
    }

    @Test
    void should_manage_missing_content() throws IOException, InterruptedException {
        // GIVEN
        this.mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        this.mockWebServer.start();
        // WHEN
        PageResult result = tested.retrievePageContent(this.mockWebServer.url("/").toString());
        // THEN
        Assertions.assertEquals(404,result.httpStatusCode());
        Assertions.assertEquals("",result.body());
        // AND
        this.mockWebServer.shutdown();
    }
}