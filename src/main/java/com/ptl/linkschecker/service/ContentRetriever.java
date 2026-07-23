package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.LinksClassifier;
import com.ptl.linkschecker.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

@Slf4j
public class ContentRetriever {

    private static final Duration RETRY_BACKOFF = Duration.ofMillis(300);

    private final HttpClient httpClient;
    private final Duration timeout;
    private final int maxAttempts;

    public ContentRetriever(HttpClient httpClient, Duration timeout) {
        this(httpClient, timeout, 3);
    }

    public ContentRetriever(HttpClient httpClient, Duration timeout, int maxAttempts) {
        this.httpClient = httpClient;
        this.timeout = timeout;
        this.maxAttempts = maxAttempts;
    }

    public PageResult retrievePageContent(String url) throws InterruptedException {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            boolean lastAttempt = attempt == maxAttempts;
            try {
                UrlUtils.validateScheme(url);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .GET()
                        .timeout(timeout)
                        .setHeader("User-Agent",
                                "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0")
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (isTransientFailure(response.statusCode()) && !lastAttempt) {
                    Thread.sleep(RETRY_BACKOFF.multipliedBy(attempt));
                    continue;
                }
                if (LinksClassifier.isRedirectLink(response.statusCode())){
                    return new PageResult(url, response.headers().firstValue("Location").orElse(null), response.statusCode());
                }
                return new PageResult(url, response.body(), response.statusCode());
            } catch (HttpTimeoutException _){
                if (lastAttempt) return new PageResult(url, "Request timed out", 408);
                Thread.sleep(RETRY_BACKOFF.multipliedBy(attempt));
            } catch (ConnectException _){
                if (lastAttempt) return new PageResult(url, "Unable to reach url", 404);
                Thread.sleep(RETRY_BACKOFF.multipliedBy(attempt));
            } catch (IllegalArgumentException | IOException e){
                return new PageResult( url, e.getMessage(), 500);
            }
        }
        throw new IllegalStateException("Unreachable: loop always returns or throws before exhausting attempts");
    }

    /** 408/429/5xx are worth a retry: they're commonly transient (rate limiting, momentary server trouble). */
    private boolean isTransientFailure(int statusCode) {
        return statusCode == 408 || statusCode == 429 || statusCode >= 500;
    }
}
