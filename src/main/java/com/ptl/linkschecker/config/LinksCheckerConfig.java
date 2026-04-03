package com.ptl.linkschecker.config;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.service.*;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class LinksCheckerConfig {

    @Value("${links-checker.request-timeout-seconds:10}")
    private int requestTimeoutSeconds;

    @Value("${links-checker.max-connections-per-host:2}")
    private int maxConnectionsPerHost;

    @Value("${links-checker.max-parallel-requests:10}")
    private int maxParallelRequests;

    @Bean
    public HttpClient getHttpClient(){ return HttpClient.newHttpClient(); }

    @Bean
    public ContentRetriever contentRetriever(@Autowired HttpClient client){
        return new ContentRetriever(client, Duration.ofSeconds(requestTimeoutSeconds));
    }

    @Bean
    LinkRetriever linkRetriever(){ return new LinkRetriever(); }

    @Bean
    LinksManager linksManager(){ return new LinksManager(); }

    @Bean
    LinksCrawler linksCrawler(@Autowired ContentRetriever contentRetriever,
                              @Autowired LinkRetriever linkRetriever,
                              @Autowired LinksManager linksManager){
        return new LinksCrawler(contentRetriever, linkRetriever, linksManager, maxConnectionsPerHost, maxParallelRequests);
    }

    @Bean
    public ProgressCounter progressCounter() { return new ProgressCounter(); }
}
