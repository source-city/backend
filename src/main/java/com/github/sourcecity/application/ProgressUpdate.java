package com.github.sourcecity.application;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

public class ProgressUpdate extends ApplicationEvent {

    private final String repositoryId;
    private final String repositoryName;
    private final String repositoryUrl;
    private final int processed;
    private final int total;

    public ProgressUpdate(String repositoryId, String repositoryName, String repositoryUrl, int processed, int total) {
        super(repositoryId);
        this.repositoryId = repositoryId;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
        this.processed = processed;
        this.total = total;
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("repositoryId", repositoryId);
        map.put("repositoryName", repositoryName);
        map.put("repositoryUrl", repositoryUrl);
        map.put("processed", processed);
        map.put("total", total);
        return map;
    }
}
