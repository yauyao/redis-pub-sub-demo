package com.example.redis.demo;

import com.example.redis.demo.model.SendMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final RedisPublisher publisher;

    public LogController(RedisPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public Mono<String> sendLog(@RequestBody SendMessage log) {
        return publisher.publish(log)
                .thenReturn("✅ Log published");
    }
}

