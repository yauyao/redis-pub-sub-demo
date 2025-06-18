package com.example.redis.demo;

import com.example.redis.demo.model.SendMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Log4j2
@Service
public class RedisPublisher {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RedisPublisher(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Long> publish(SendMessage message) {
        String key = "log:" + System.currentTimeMillis();
        String content = message.getContent();
        String channel = message.getChannel();

        // 儲存資料並設定過期時間
        Mono<Boolean> storeWithTTL = redisTemplate
                .opsForValue()
                .set(key, content, Duration.ofSeconds(5))
                .doOnNext(success -> {
                    if (success) {
                        log.info("成功儲存訊息至 Redis：channel={}, content={}", channel, content);
                    } else {
                        log.warn("儲存訊息失敗：channel={}, content={}", channel, content);
                    }
                })
                .onErrorResume(e -> {
                    log.error("儲存訊息時發生錯誤", e);
                    return Mono.just(false);
                });

        // 發布訊息
        Mono<Long> publishMessage = redisTemplate
                .convertAndSend(channel, content)
                .doOnNext(count -> log.info("成功發佈訊息：channel={}, content={}, receivers={}", channel, content, count))
                .retry(3)
                .onErrorResume(e -> {
                    log.error("發佈訊息失敗：channel={}, content={}", channel, content, e);
                    return Mono.just(0L);
                });

        // 同時執行兩個操作
        return storeWithTTL.then(publishMessage);
    }
}
