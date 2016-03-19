package com.github.sourcecity.application;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

public class ProgressUpdate extends ApplicationEvent {

    private final String id;
    private final String name;
    private final String url;
    private final int processed;
    private final int total;

    public ProgressUpdate(String id, String name, String url, int processed, int total) {
        super(id);
        this.id = id;
        this.name = name;
        this.url = url;
        this.processed = processed;
        this.total = total;
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("url", url);
        map.put("processed", processed);
        map.put("total", total);
        return map;
    }
}
