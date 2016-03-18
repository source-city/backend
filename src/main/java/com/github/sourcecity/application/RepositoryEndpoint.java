package com.github.sourcecity.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("repositories")
public class RepositoryEndpoint {

    @Autowired
    private MongoTemplate mongo;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> register(@RequestBody RepositoryJson definition) {
        CompletableFuture.runAsync(() -> {
            try (InputStream stream = repositoryUrl(definition.url).openStream()) {
                ZipInputStream zipInputStream = new ZipInputStream(stream);
                ZipEntry zipEntry = null;
                RepositoryMetrics repositoryMetrics = new RepositoryMetrics(definition.url, definition.name);
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    if (!zipEntry.isDirectory() && fileName.endsWith(".java")) {
                        String entryContent = IOUtils.toString(zipInputStream);
                        System.out.println("-------------" + fileName);
                        System.out.println(entryContent);
                        int loc = StringUtils.countMatches(entryContent, '\n');
                        System.out.println("------------- loc: " + loc);
                        repositoryMetrics.add(fileName, loc);
                    }
                }
                mongo.insert(repositoryMetrics);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.accepted().build();
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

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoryJson {
        String url;
        String name;
    }
}
