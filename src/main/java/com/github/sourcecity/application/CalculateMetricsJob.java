package com.github.sourcecity.application;

import org.apache.commons.io.IOUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class CalculateMetricsJob implements Runnable {

    private final String repositoryUrl;
    private final String repositoryName;
    private final MongoTemplate mongo;

    public CalculateMetricsJob(String repositoryUrl, String repositoryName, MongoTemplate mongo) {
        this.repositoryUrl = repositoryUrl;
        this.repositoryName = repositoryName;
        this.mongo = mongo;
    }

    @Override
    public void run() {
        URLConnection urlConnection = null;
        try {
            urlConnection = repositoryUrl(repositoryUrl).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        int total = urlConnection.getContentLength();
        try (InputStream stream = urlConnection.getInputStream()) {

            ProgressCapturingStream progress = new ProgressCapturingStream(stream);

            RepositoryMetrics repositoryMetrics = new RepositoryMetrics(repositoryUrl, repositoryName);
            ZipInputStream zipInputStream = new ZipInputStream(progress);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                if (!zipEntry.isDirectory() && fileName.endsWith(".java")) {
                    String fileContent = IOUtils.toString(zipInputStream);
                    repositoryMetrics.collectFrom(fileName, fileContent);

                    System.err.println("Progress: " + progress.getProgress() + " of " + total);
                }
            }
            store(repositoryMetrics);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    private void store(RepositoryMetrics repositoryMetrics) {
        String collectionName = RepositoryMetrics.COLLECTION_NAME;
        if (mongo.exists(new Query().addCriteria(where("_id").is(repositoryMetrics.id())), collectionName)) {
            mongo.remove(repositoryMetrics, collectionName);
        }
        mongo.insert(repositoryMetrics);
    }

    private URL repositoryUrl(String url) {
        url = url.replaceFirst("\\.git", "/archive/master.zip");
        URI repositoryUri = URI.create(url);
        try {
            return repositoryUri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
