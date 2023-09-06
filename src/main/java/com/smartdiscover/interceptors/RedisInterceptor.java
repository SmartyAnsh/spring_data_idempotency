package com.smartdiscover.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartdiscover.entities.Customer;
import com.smartdiscover.services.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.UUID;

@Component
public class RedisInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RedisInterceptor.class);

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getMethod().equals("GET")) {
            if (null != request.getHeader("idempotencyKey")) {
                UUID idempotencyKey = UUID.fromString(request.getHeader("idempotencyKey"));

                log.info("intercepted request for getCustomer with idempotencyKey: {}", idempotencyKey);

                Customer customer = redisService.getCustomer(idempotencyKey);

                if (null != customer) {
                    log.info("got Customer: {} from redisService with idempotencyKey: {}", customer, idempotencyKey);

                    ObjectMapper mapper = new ObjectMapper();
                    response.setStatus(200);

                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(mapper.writeValueAsString(customer));
                    out.flush();

                    return false;
                } else {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }

    }

}