package com.smartdiscover.services;

import com.smartdiscover.entities.Customer;
import com.smartdiscover.util.PojoHashMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private HashOperations redisHashOperator;

    public RedisService(RedisTemplate redisTemplate) {
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        this.redisTemplate = redisTemplate;
        this.redisHashOperator = this.redisTemplate.opsForHash();
    }

    public Customer getCustomer(UUID idempotencyKey) {
        log.info("getCustomer for idempotencyKey: {}", idempotencyKey);

        Customer customer = null;

        HashMapper<Customer, String, String> bookHashMapper = new DecoratingStringHashMapper<>(new PojoHashMapper<>(Customer.class));

        if (redisTemplate.hasKey(idempotencyKey)) {
            customer = bookHashMapper.fromHash(redisHashOperator.entries(idempotencyKey));
        }
        return customer;
    }

    public void saveCustomer(Customer customer, UUID idempotencyKey) {
        log.info("saveCustomer for idempotencyKey: {}", idempotencyKey);

        //Hash operations
        Map customerMap = new HashMap<String, Object>();
        customerMap.put("id", customer.getId().toString());
        customerMap.put("firstName", customer.getFirstName());
        customerMap.put("lastName", customer.getLastName());
        customerMap.put("email", customer.getEmail());

        redisHashOperator.putAll(idempotencyKey, customerMap);
    }
}
