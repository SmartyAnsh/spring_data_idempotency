package com.smartdiscover.services;

import com.smartdiscover.entities.Customer;
import com.smartdiscover.repos.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RedisService redisService;

    public Customer saveCustomer(Customer customer) {
        customer = customerRepository.save(customer);

        log.info("saved customer: {}", customer);

        return customer;
    }

    public Customer getCustomer(UUID customerId, UUID idempotencyKey) {
        log.info("getCustomer for customerId: {}", customerId);

        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (null != customer) {
            redisService.saveCustomer(customer, idempotencyKey);
        }

        return customer;
    }
}
