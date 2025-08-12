package com.anischedule.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class APICache {
    
    private final JedisPool pool;
    private final int ttl = 86400 * 1000; // TTL: 1 day

    public APICache() {
        JedisPoolConfig config = new JedisPoolConfig();
        this.pool = new JedisPool(config, getRedisUrl());
    }

    public APICache(JedisPool pool) {
        this.pool = pool;
    }

    public <T> T get(String key) {
        T value = null;
        try (Jedis jedis = pool.getResource()) {
            String cached = jedis.get(key);
            ObjectMapper mapper = new ObjectMapper();
            value = mapper.readValue(cached, new TypeReference<T>() {});
            System.out.println("Cache Hit: " + key);
        } catch (Exception e) {
            System.out.println("Cache Miss: " + key);
            System.err.println("Could not get cached value for " + key + ": " + e);
        }
        return value;
    }

    public <T> boolean set(String key, T value) {
        try (Jedis jedis = pool.getResource()) {
            ObjectMapper mapper = new ObjectMapper();
            jedis.set(key, mapper.writeValueAsString(value));
            jedis.pexpire(key, ttl);
            return true;
        } catch (Exception e) {
            System.err.println("Could not set cached value for " + key + ": " + e);
        }
        return false;
    }

    private static String getRedisUrl() {
        String url = System.getenv("REDIS_URL");
        return url != null ? url : "redis://localhost:6379";
    }

}
