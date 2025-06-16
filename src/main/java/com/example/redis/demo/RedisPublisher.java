package com.example.redis.demo;


import com.example.redis.demo.model.SendMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
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
        // 儲存資料並設定過期時間（例如 1 小時）
        Mono<Boolean> storeWithTTL = redisTemplate
                .opsForValue()
                .set("log:" + System.currentTimeMillis(), message.getContent(), Duration.ofSeconds(5))
                .doOnNext(msg->{
                    ThreadContext.put("subscriberId", message.getChannel()); // 動態設定
                    log.info("收到訂閱資料：{}", message.getContent());
                    ThreadContext.clearAll(); // 記得清除，避免污染其他 log
                });

        // 發布訊息
        Mono<Long> publishMessage = redisTemplate.convertAndSend(message.getChannel(), message.getContent());

        // 同時執行兩個操作
        return storeWithTTL.then(publishMessage);
    }
}
