package com.github.sourcecity.application;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositoryMetrics {

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("^import ", Pattern.MULTILINE);
    public static final String COLLECTION_NAME = "repositoryMetrics";

    private final String id;
    private final String name;
    private final List<FileMetrics> fileMetrics = new ArrayList<>();

    public RepositoryMetrics(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void collectFrom(String fileName, String sourceCode) {
        int loc = calculateLOC(sourceCode);
        int dependencies = calculateDependencies(sourceCode);
        fileMetrics.add(new FileMetrics(fileName, loc, dependencies));
    }
    public void forEachFile(Consumer<FileMetrics> consumer){
        fileMetrics.forEach(consumer);
    }

    private int calculateDependencies(String sourceCode) {
        Matcher matcher = DEPENDENCY_PATTERN.matcher(sourceCode);
        int dependencies = 0;
        while (matcher.find()) {
            dependencies++;
        }
        return dependencies;
    }

    private int calculateLOC(String sourceCode) {
        return StringUtils.countMatches(sourceCode, '\n');
    }

    public String id() {
        return id;
    }

    private static class FileMetrics {

        private final String fileName;
        private final int loc;
        private final int dependencies;

        public FileMetrics(String fileName, int loc, int dependencies) {
            this.fileName = fileName;
            this.loc = loc;
            this.dependencies = dependencies;
        }
    }
}
