package com.gihub.dimonekun.rssfeed;

import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.feed.dsl.Feed;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@EnableConfigurationProperties
@EnableIntegration
@SpringBootApplication
@RestController
public class RssFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssFeedApplication.class, args);
    }

    @Autowired IntegrationFlowContext flowContext;

    @GetMapping
    @CrossOrigin(origins = "*")
    public Flux<SyndEntry> feed() {
        return Flux.create(sink -> {
            final MessageHandler handler = message -> sink.next((SyndEntry) message.getPayload());
            sink.onCancel(() -> feedSubscribeChannel().unsubscribe(handler));
            sink.onDispose(() -> feedSubscribeChannel().unsubscribe(handler));
            feedSubscribeChannel().subscribe(handler);
        });
    }

    @PostConstruct
    public void feedFlow() throws MalformedURLException {
        for (Map.Entry<String, FeedConfig> entry : feedProperties().entrySet()) {
            final URL feedUrl = new URL(entry.getValue().getUri());
            final FeedConfig feedConfig = entry.getValue();

            flowContext.registration(IntegrationFlows
                    .from(
                            Feed.inboundAdapter(feedUrl, "rss"),
                            e -> e.poller(p -> p.fixedDelay(feedConfig.getDelay() != null ? feedConfig.getDelay() : 1000))
                    )
                    .channel(feedSubscribeChannel())
                    .get()
            ).register();
        }
    }

    @Bean
    public SubscribableChannel feedSubscribeChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    @ConfigurationProperties(prefix = "feed")
    public Map<String, FeedConfig> feedProperties() {
        return new HashMap<>();
    }

    public static class FeedConfig {
        String uri;
        Long delay;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public Long getDelay() {
            return delay;
        }

        public void setDelay(Long delay) {
            this.delay = delay;
        }
    }
}
