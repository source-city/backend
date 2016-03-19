package com.github.sourcecity.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RepositoriesEndpoint {

    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Autowired
    private MongoTemplate mongo;

    @RequestMapping(value = "/repositories", method = GET)
    public List list() {
        return Stream.concat(readyRepositories(), notReadyRepositories()).collect(toList());
    }

    private Stream<RepositoryJson> notReadyRepositories() {
        return mongo.find(new Query(), ScheduledMetrics.class).stream().map(RepositoryJson::new);
    }

    private Stream<RepositoryJson> readyRepositories() {
        return mongo.find(new Query(), RepositoryMetrics.class).stream().map(RepositoryJson::new);
    }

    @RequestMapping("/updates")
    public SseEmitter updates() {

        SseEmitter updates = new SseEmitter();
        listenForUpdates(updates);

        return updates;
    }

    private void listenForUpdates(SseEmitter updates) {
        emitters.add(updates);
    }

    @EventListener(classes = ProgressUpdate.class)
    public void onProgressUpdate(ProgressUpdate update){
        emitters.forEach(emitter -> {
            try {
                emitter.send(update.asMap(), MediaType.APPLICATION_JSON_UTF8);
            } catch (IOException | IllegalStateException e) {
                emitters.remove(emitter);
            }
        });
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class RepositoryJson {

        String id;
        String name;
        String url;
        boolean ready;

        public RepositoryJson(RepositoryMetrics repositoryMetrics) {
            id = repositoryMetrics.id();
            name = repositoryMetrics.name();
            url = repositoryMetrics.url();
            ready = true;
        }

        public RepositoryJson(ScheduledMetrics scheduledMetrics) {
            id = scheduledMetrics.id();
            name = scheduledMetrics.name();
            url = scheduledMetrics.url();
            ready = false;
        }
    }
}
