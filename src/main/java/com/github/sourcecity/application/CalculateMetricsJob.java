package com.github.sourcecity.application;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class CalculateMetricsJob {


    @Autowired
    private MongoTemplate mongo;

    @Autowired
    private ApplicationEventPublisher publisher;

    public void schedule(String name, String url) {

        CompletableFuture.runAsync(new Job(name, url));

    }

    private class Job implements Runnable {

        private final String repositoryUrl;
        private final String repositoryName;

        public Job(String name, String url) {
            this.repositoryName = name;
            this.repositoryUrl = url;
        }

        @Override
        public void run() {
            URLConnection urlConnection;
            try {
                urlConnection = repositoryUrl(repositoryUrl).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            int total = urlConnection.getContentLength();

            try (InputStream stream = urlConnection.getInputStream()) {

                ProgressCapturingStream progress = new ProgressCapturingStream(stream);

                RepositoryMetrics repositoryMetrics = new RepositoryMetrics(RepositoryId.generate(repositoryUrl), repositoryName);
                ZipInputStream zipInputStream = new ZipInputStream(progress);
                ZipEntry zipEntry;
                int lastUpdate = 0;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    if (!zipEntry.isDirectory() && fileName.endsWith(".java")) {
                        String fileContent = IOUtils.toString(zipInputStream);
                        repositoryMetrics.collectFrom(fileName, fileContent);
                    }
                    int processed = progress.getProgress();
                    if (processed - lastUpdate > 5000) {
                        publisher.publishEvent(new ProgressUpdate(repositoryName, processed, total));
                        System.err.println("Progress: " + processed + " of " + total);

                        if (total < 0) {
                            total = fetchTotal(repositoryUrl);
                        }
                        lastUpdate = processed;
                    }
                }
                publisher.publishEvent(new ProgressUpdate(repositoryName, total, total));
                store(repositoryMetrics);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }

        private int fetchTotal(String repositoryUrl) {

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) repositoryUrl(repositoryUrl).openConnection();
                urlConnection.setRequestMethod("HEAD");

                return urlConnection.getContentLength();

            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        private void store(RepositoryMetrics repositoryMetrics) {
            Query idQuery = new Query().addCriteria(where("_id").is(repositoryMetrics.id()));
            mongo.remove(idQuery, ScheduledMetrics.class);
            if (mongo.exists(idQuery, RepositoryMetrics.class)) {
                mongo.remove(idQuery, RepositoryMetrics.class);
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
}
