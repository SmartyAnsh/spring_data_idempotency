package com.smartdiscover.controllers;

import com.smartdiscover.entities.Customer;
import com.smartdiscover.services.ApiService;
import com.smartdiscover.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public Customer getCustomer(@RequestHeader(name="idempotencyKey") UUID idempotencyKey, @RequestParam(name = "id") UUID id) {
        return apiService.getCustomer(id, idempotencyKey);
    }

    @PostMapping
    public Customer saveCustomer(@RequestBody Customer customer) {
        return apiService.saveCustomer(customer);
    }


}
