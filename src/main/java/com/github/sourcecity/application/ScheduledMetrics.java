package com.github.sourcecity.application;

public class ScheduledMetrics {

    private final String id;
    private final String name;
    private final String url;

    public ScheduledMetrics(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }
}
