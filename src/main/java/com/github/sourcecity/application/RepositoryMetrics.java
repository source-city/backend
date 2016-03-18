package com.github.sourcecity.application;

import java.util.ArrayList;
import java.util.List;

public class RepositoryMetrics {

    private final String id;
    private final String name;
    private final List<FileMetrics> fileMetrics = new ArrayList<>();

    public RepositoryMetrics(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void add(String fileName, int loc) {
        fileMetrics.add(new FileMetrics(fileName, loc));
    }

    private static class FileMetrics {

        private final String fileName;
        private final int loc;

        public FileMetrics(String fileName, int loc) {
            this.fileName = fileName;
            this.loc = loc;
        }
    }
}
