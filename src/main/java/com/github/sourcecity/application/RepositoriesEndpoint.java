package com.github.sourcecity.application;

import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class RepositoriesEndpoint {

    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @RequestMapping("/repositories")
    public List list() {
        return Collections.emptyList();
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
                emitter.send(update.asMap());
            } catch (IOException | IllegalStateException e) {
                emitters.remove(emitter);
            }
        });
    }


}
