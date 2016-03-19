package com.github.sourcecity.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("metrics")
public class MetricsEndpoint {

    @Autowired
    private MongoTemplate mongo;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value="/{metricsId}")
    public Map<String, Object> list(@PathVariable String metricsId) throws UnsupportedEncodingException {
        String decodedMetricsId = new String(Base64.getDecoder().decode(metricsId), "UTF-8");
        Map<String, Object> repository = mongo.findById(decodedMetricsId, Map.class, RepositoryMetrics.COLLECTION_NAME);
        repository.remove("_id");
        repository.remove("_class");

        return repository;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private class MetricsJson {
        String label;
        Map<String, BigDecimal> metrics = new HashMap<>();
    }
}
