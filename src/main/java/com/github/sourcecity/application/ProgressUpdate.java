package com.github.sourcecity.application;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

public class ProgressUpdate extends ApplicationEvent {

    private final String repositoryName;
    private final Integer progress;
    private final int total;

    public ProgressUpdate(String repositoryName, Integer progress, int total) {
        super(repositoryName);
        this.repositoryName = repositoryName;
        this.progress = progress;
        this.total = total;
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("repositoryName", repositoryName);
        map.put("progress", progress);
        map.put("total", total);
        return map;
    }
}
