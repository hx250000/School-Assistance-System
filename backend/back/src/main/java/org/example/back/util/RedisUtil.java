package org.example.back.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


import redis.clients.jedis.Jedis;
@Component
public class RedisUtil {

    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    public static void set(String key, String value) {
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            jedis.set(key, value);
        }
    }

    public static String get(String key) {
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            return jedis.get(key);
        }
    }
}