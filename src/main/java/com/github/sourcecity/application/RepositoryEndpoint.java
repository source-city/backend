package com.github.sourcecity.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("repositories")
public class RepositoryEndpoint {

    @Autowired
    private MongoTemplate mongo;
    @Autowired
    private CalculateMetricsJob job;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> register(@RequestBody RepositoryJson definition) {
        job.schedule(definition.name, definition.url);
        return ResponseEntity.accepted().build();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoryJson {
        String url;
        String name;
    }
}
