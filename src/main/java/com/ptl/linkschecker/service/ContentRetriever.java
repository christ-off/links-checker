package com.ptl.linkschecker.service;

import com.ptl.linkschecker.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ContentRetriever {

    private final HttpClient httpClient;

    public PageResult retrievePageContent(String url) throws InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .setHeader("User-Agent",
                            "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 301 || response.statusCode() == 302){
                return new PageResult(url, response.headers().firstValue("Location"), response.statusCode());
            }
            return new PageResult(url, Optional.ofNullable(response.body()), response.statusCode());
        } catch (ConnectException ce){
            return new PageResult( url , Optional.of("Unable to reach url"), 404);
        } catch (IllegalArgumentException | IOException e){
            return new PageResult( url, Optional.of(e.getMessage()), 500);
        }
    }
}
