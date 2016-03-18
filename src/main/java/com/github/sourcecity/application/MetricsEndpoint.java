package com.github.sourcecity.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@RestController
@RequestMapping("metrics")
public class MetricsEndpoint {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<MetricsJson> list() {
        MetricsJson metrics = new MetricsJson();
        metrics.label = "com.bla.BlaBla";
        metrics.metrics.put("loc", new BigDecimal("100"));
        metrics.metrics.put("dependencies", new BigDecimal("7"));
        return asList(metrics);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private class MetricsJson {
        String label;
        Map<String, BigDecimal> metrics = new HashMap<>();
    }
}
