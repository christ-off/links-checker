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

@RequiredArgsConstructor
@Slf4j
public class ContentRetrieverImpl implements ContentRetriever {

    private final HttpClient httpClient;

    @Override
    public PageResult retrievePageContent(String url) throws InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new PageResult(response.body(), response.statusCode());
        } catch (ConnectException ce){
            log.warn("Unable to reach {}",url);
            return new PageResult(null, 404);
        } catch (IllegalArgumentException | IOException e){
            log.warn("Error while processing {} - {} ",url,e.getMessage());
            return new PageResult(null, 500);
        }
    }
}
