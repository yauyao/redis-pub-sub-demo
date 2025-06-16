package com.example.redis.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;


@Service
public class RedisSubscriber {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RedisSubscriber(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void subscribe() {
        redisTemplate.listenTo(ChannelTopic.of("log-channel"))
                .map(message -> message.getMessage())
                .doOnNext(msg -> System.out.println("📥 Received: " + msg))
                .subscribe();
    }
}