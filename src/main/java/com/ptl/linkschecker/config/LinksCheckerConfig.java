package com.ptl.linkschecker.config;

import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.core.LinksCrawlerImpl;
import com.ptl.linkschecker.service.*;
import com.ptl.linkschecker.utils.ProgressCounter;
import com.ptl.linkschecker.utils.ProgressCounterImpl;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.http.HttpClient;

@Configuration
public class LinksCheckerConfig {

    @Bean
    public HttpClient getHttpClient(){
        return HttpClient.newHttpClient();
    }

    @Bean
    public ContentRetriever contentRetriever(@Autowired HttpClient client){
        return new ContentRetrieverImpl(client);
    }

    @Bean
    LinkRetriever linkRetriever(){ return new LinkRetrieverImpl(); }

    @Bean
    LinksManager linksManager(){ return new LinksManagerImpl(); }

    @Bean
    LinksCrawler linksCrawler(@Autowired ContentRetriever contentRetriever,
                              @Autowired LinkRetriever linkRetriever,
                              @Autowired LinksManager linksManager){
        return new LinksCrawlerImpl(contentRetriever,linkRetriever,linksManager);
    }

    @Bean
    public ProgressCounter progressCounter(@Lazy Terminal terminal) {
        return new ProgressCounterImpl(terminal);
    }
}
