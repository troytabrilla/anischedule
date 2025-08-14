package com.anischedule.cache;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class APICacheTests {
    @Mock
    private JedisPool pool;

    @Mock
    private Jedis instance;

    @InjectMocks
    private APICache cache;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkCacheHit() {
        Mockito.when(instance.get(Mockito.anyString())).thenReturn("{\"hit\":true}");
        Mockito.when(pool.getResource()).thenReturn(instance);
        
        Map<String, Object> expected = new HashMap<>();
        expected.put("hit", true);

        Map<String, Object> actual = cache.get("test");

        Assertions.assertEquals(expected, actual);
    }
    
    @Test
    public void checkCacheMiss() {
        Mockito.when(instance.get(Mockito.anyString())).thenThrow(new RuntimeException("miss"));
        Mockito.when(pool.getResource()).thenReturn(instance);
        
        Map<String, Object> expected = null;

        Map<String, Object> actual = cache.get("test");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void checkValidCacheWrite() {
        String key = "test";
        String value = "value";
        Mockito.when(instance.set(Mockito.anyString(), Mockito.any())).thenReturn("OK");
        Mockito.when(instance.pexpire(Mockito.anyString(), Mockito.anyLong())).thenReturn(Long.valueOf(0));
        Mockito.when(pool.getResource()).thenReturn(instance);

        boolean actual = cache.set(key, value);

        Assertions.assertTrue(actual);
    }

    @Test
    void checkInvalidCacheWrite() {
        String key = "test";
        String value = "value";
        RuntimeException e = new RuntimeException("fail");
        Mockito.when(instance.set(Mockito.anyString(), Mockito.any())).thenThrow(e);
        Mockito.when(pool.getResource()).thenReturn(instance);
        
        boolean actual = cache.set(key, value);

        Assertions.assertFalse(actual);
    }

}
