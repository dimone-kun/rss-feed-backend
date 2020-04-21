package com.gihub.dimonekun.rssfeed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;

import java.util.HashMap;
import java.util.Map;

@EnableIntegration
@EnableConfigurationProperties
@Configuration
public class IntegrationConfiguration {

    @Bean("feedChannel")
    public SubscribableChannel feedChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean("feedProperties")
    @ConfigurationProperties(prefix = "feed")
    public Map<String, FeedConfig> feedProperties() {
        return new HashMap<>();
    }
}
