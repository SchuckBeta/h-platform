package com.hch.platform.pcore.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by victor on 2017/8/14.
 * redis spring-data-redis 使用
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-context-jedis-cluster.xml")
public class TestRedisCluster {
    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void testRedisInfo() {
        System.out.println("testAloneInfo...");
        redisTemplate.execute(new RedisCallback<Integer>() {
            //这里返回值是个上面的RedisCallback<Integer> 中的泛型一直，
            public Integer doInRedis(RedisConnection connection) {
                int i = 0;
                for (; i < 100; i++) {
                    byte[] key = ("key:" + i).getBytes();
                    byte[] value = ("value:" + i).getBytes();
                    connection.set(key, value);
                }
                //这里返回值是个上面的RedisCallback<Integer> 中的泛型一直，
                return i;

            }
        });
    }


}
