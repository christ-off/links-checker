package com.ptl.linkschecker.config;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.service.*;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.http.HttpClient;

@Configuration
public class LinksCheckerConfig {

    @Bean
    public HttpClient getHttpClient(){ return HttpClient.newHttpClient(); }

    @Bean
    public ContentRetriever contentRetriever(@Autowired HttpClient client){
        return new ContentRetriever(client);
    }

    @Bean
    LinkRetriever linkRetriever(){ return new LinkRetriever(); }

    @Bean
    LinksManager linksManager(){ return new LinksManager(); }

    @Bean
    LinksCrawler linksCrawler(@Autowired ContentRetriever contentRetriever,
                              @Autowired LinkRetriever linkRetriever,
                              @Autowired LinksManager linksManager){
        return new LinksCrawler(contentRetriever,linkRetriever,linksManager);
    }

    @Bean
    public ProgressCounter progressCounter(@Lazy Terminal terminal) {
        return new ProgressCounter(terminal);
    }
}
