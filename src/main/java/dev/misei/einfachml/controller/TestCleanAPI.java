package dev.misei.einfachml.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RestController
@AllArgsConstructor
@RequestMapping("/api/config")
@Slf4j
public class TestCleanAPI {

    private ReactiveMongoTemplate mongoTemplate;

    @GetMapping("/boom")
    public Mono<Void> boom() {
        return mongoTemplate.getCollectionNames().flatMap((Function<String, Publisher<?>>) s -> mongoTemplate.dropCollection(s)).then();
    }
}