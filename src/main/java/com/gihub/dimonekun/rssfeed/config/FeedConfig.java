package com.gihub.dimonekun.rssfeed.config;

import java.net.URL;

public class FeedConfig {
    URL uri;
    Long delay;

    public URL getUri() {
        return uri;
    }

    public void setUri(URL uri) {
        this.uri = uri;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }
}
