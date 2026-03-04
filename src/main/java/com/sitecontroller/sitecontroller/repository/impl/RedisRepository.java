package com.sitecontroller.sitecontroller.repository.impl;

import java.time.Instant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.sitecontroller.sitecontroller.repository.IRedisRepository;

@Repository
public class RedisRepository implements IRedisRepository {

    private static final Log logger = LogFactory.getLog(RedisRepository.class);

    // TO DO: Confirm exact Redis data structure and update this constant accordingly
    private static final String ACTIVE_FIELD = "active";

    private final StringRedisTemplate redisTemplate;

    public RedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getGatewaySyncValue(String deviceEui) {
        logger.debug("RedisRepository - getGatewaySyncValuecalled with deviceEui: " + deviceEui);
        Object value = redisTemplate.opsForHash().get(deviceEui, "GatewaySyncValue");
        if (value == null) {
            return null;
        }
        return (String) value;
    }

    @Override
    public String setPendingGatewaySyncValue(String deviceEui){
        
        logger.debug("RedisRepository - setPendingGatewaySyncValue called with deviceEui: " + deviceEui);
        redisTemplate.opsForHash().put(deviceEui, "GatewaySyncValue", "pending");
        return "TO DO";
    }
}
