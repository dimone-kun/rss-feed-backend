package com.gihub.dimonekun.rssfeed;

import com.gihub.dimonekun.rssfeed.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.feed.dsl.Feed;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Service
public class FeedService {
    private final IntegrationFlowContext flowContext;
    private final Map<String, FeedConfig> feedProperties;
    private final SubscribableChannel feedChannel;

    public FeedService(IntegrationFlowContext flowContext,
                       @Qualifier("feedProperties") Map<String, FeedConfig> feedProperties,
                       @Qualifier("feedChannel") SubscribableChannel feedChannel) {
        this.flowContext = flowContext;
        this.feedProperties = feedProperties;
        this.feedChannel = feedChannel;
    }

    @PostConstruct
    public void feedFlow() throws MalformedURLException {
        for (Map.Entry<String, FeedConfig> entry : feedProperties.entrySet()) {
            this.registerFeed(entry.getValue());
        }
    }

    public Flux<SyndEntry> feed() {
        return Flux.create(sink -> {
            final MessageHandler handler = message -> sink.next((SyndEntry) message.getPayload());
            sink.onCancel(() -> feedChannel.unsubscribe(handler));
            sink.onDispose(() -> feedChannel.unsubscribe(handler));
            feedChannel.subscribe(handler);
        });
    }

    public void registerFeed(final FeedConfig feedConfig) throws MalformedURLException {
        final URL feedUrl = new URL(feedConfig.getUri());

        flowContext.registration(IntegrationFlows
                .from(
                        Feed.inboundAdapter(feedUrl, "rss"),
                        e -> e.poller(p -> p.fixedDelay(feedConfig.getDelay() != null ? feedConfig.getDelay() : 1000))
                )
                .channel(feedChannel)
                .get()
        ).register();
    }
}
