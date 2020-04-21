package com.gihub.dimonekun.rssfeed.config;

public class FeedConfig {
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
