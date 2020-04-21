package com.gihub.dimonekun.rssfeed;

import com.gihub.dimonekun.rssfeed.config.FeedConfig;
import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.net.URL;

@CrossOrigin(origins = "*")
@RestController
public class FeedController {
    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    @CrossOrigin(origins = "*")
    public Flux<SyndEntry> feed() {
        return feedService.feed();
    }

    @PostMapping(path = "{feedId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void putFeed(@PathVariable("feedId") String feedId,
                        @RequestBody URL feedUrl) {
        FeedConfig newFeed = new FeedConfig();
        newFeed.setUri(feedUrl);

        feedService.registerFeed(newFeed);
    }
}
