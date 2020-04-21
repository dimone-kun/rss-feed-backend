package com.gihub.dimonekun.rssfeed;

import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}
