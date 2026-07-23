package com.ptl.linkschecker.config;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.service.ContentRetriever;
import com.ptl.linkschecker.service.LinkRetriever;
import com.ptl.linkschecker.service.LinksManager;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(LinksCheckerProperties.class)
public class LinksCheckerConfig {

    @Bean
    public HttpClient httpClient() { return HttpClient.newHttpClient(); }

    @Bean
    public ContentRetriever contentRetriever(HttpClient client, LinksCheckerProperties props) {
        return new ContentRetriever(client, Duration.ofSeconds(props.requestTimeoutSeconds()), props.maxRequestAttempts());
    }

    @Bean
    LinkRetriever linkRetriever() { return new LinkRetriever(); }

    @Bean
    LinksManager linksManager() { return new LinksManager(); }

    @Bean
    LinksCrawler linksCrawler(ContentRetriever contentRetriever,
                              LinkRetriever linkRetriever,
                              LinksManager linksManager,
                              SkippedSites skippedSites,
                              LinksCheckerProperties props) {
        return new LinksCrawler(contentRetriever, linkRetriever, linksManager, skippedSites, props.maxConcurrentHosts());
    }

    @Bean
    public ProgressCounter progressCounter() { return new ProgressCounter(); }

    @Bean
    SkippedSites skippedSites(LinksCheckerProperties props) {
        return new SkippedSites(props.sitesToSkip());
    }
}
