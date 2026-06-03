package com.ptl.linkschecker.config;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.service.*;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(LinksCheckerProperties.class)
public class LinksCheckerConfig {

    @Bean
    public HttpClient getHttpClient() { return HttpClient.newHttpClient(); }

    @Bean
    public ContentRetriever contentRetriever(@Autowired HttpClient client,
                                             @Autowired LinksCheckerProperties props) {
        return new ContentRetriever(client, Duration.ofSeconds(props.requestTimeoutSeconds()));
    }

    @Bean
    LinkRetriever linkRetriever() { return new LinkRetriever(); }

    @Bean
    LinksManager linksManager() { return new LinksManager(); }

    @Bean
    LinksCrawler linksCrawler(@Autowired ContentRetriever contentRetriever,
                              @Autowired LinkRetriever linkRetriever,
                              @Autowired LinksManager linksManager,
                              @Autowired SkippedSites skippedSites) {
        return new LinksCrawler(contentRetriever, linkRetriever, linksManager, skippedSites);
    }

    @Bean
    public ProgressCounter progressCounter() { return new ProgressCounter(); }

    @Bean
    SkippedSites skippedSites(@Autowired LinksCheckerProperties props) {
        return new SkippedSites(props.sitesToSkip());
    }
}