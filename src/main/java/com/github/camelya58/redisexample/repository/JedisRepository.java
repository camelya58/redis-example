package com.github.camelya58.redisexample.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * Класс JedisRepository предназначен для хранения счетчика в Redis
 *
 * @author Kamila Meshcheryakova
 */
@Repository
@RequiredArgsConstructor
public class JedisRepository {

    private final RedisConnectionFactory redisConnectionFactory;

    RedisAtomicLong redisAtomicLong;

    @Value("${app.redis.counter}")
    private String counter;

    @PostConstruct
    public void init() {
        redisAtomicLong = new RedisAtomicLong(counter, redisConnectionFactory);
    }

    /**
     * Устанавливает автоинкремент значения поля счетчика
     *
     * @return число типа long
     */
    public long get() {
        return redisAtomicLong.incrementAndGet();
    }
}
