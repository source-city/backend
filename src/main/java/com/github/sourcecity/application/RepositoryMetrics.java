package com.github.sourcecity.application;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class RepositoryMetrics {

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("^import ", Pattern.MULTILINE);
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+(.*);", Pattern.MULTILINE);

    public static final String COLLECTION_NAME = "repositoryMetrics";

    private final String id;
    private final String name;
    private final String url;
    private final List<FileMetrics> fileMetrics = new ArrayList<>();

    public RepositoryMetrics(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public void collectFrom(String fileName, String sourceCode) {
        int loc = calculateLOC(sourceCode);
        int dependencies = calculateDependencies(sourceCode);
        String classFullName = composeClassName(fileName, sourceCode);
        fileMetrics.add(new FileMetrics(classFullName, loc, dependencies));
    }

    private String composeClassName(String fileName, String sourceCode) {
        Matcher matcher = PACKAGE_PATTERN.matcher(sourceCode);

        String packageName = "";
        if (matcher.find()){
            packageName = matcher.group(1);
        }
        String classSimpleName = substringBeforeLast(substringAfterLast(fileName, "/"), ".");

        return packageName.isEmpty() ? classSimpleName : String.format("%s.%s", packageName, classSimpleName);
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

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }

    @SuppressWarnings("unused")
    private static class FileMetrics {

        private final String label;
        private final int loc;
        private final int dependencies;

        public FileMetrics(String label, int loc, int dependencies) {
            this.label = label;
            this.loc = loc;
            this.dependencies = dependencies;
        }
    }
}
