package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import com.ptl.linkschecker.utils.LinksClassifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

@Slf4j
public class ContentRetriever {

    private final HttpClient httpClient;
    private final Duration timeout;

    public ContentRetriever(HttpClient httpClient, Duration timeout) {
        this.httpClient = httpClient;
        this.timeout = timeout;
    }

    public PageResult retrievePageContent(String url) throws InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(timeout)
                    .setHeader("User-Agent",
                            "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (LinksClassifier.isRedirectLink(response.statusCode())){
                return new PageResult(url, response.headers().firstValue("Location").orElse(null), response.statusCode());
            }
            return new PageResult(url, response.body(), response.statusCode());
        } catch (HttpTimeoutException _){
            return new PageResult(url, "Request timed out", 408);
        } catch (ConnectException _){
            return new PageResult( url , "Unable to reach url", 404);
        } catch (IllegalArgumentException | IOException e){
            return new PageResult( url, e.getMessage(), 500);
        }
    }
}
