package com.example.demoservice.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {


    @GetMapping("/demo")
    public Map<String, Object> demoService(HttpServletRequest request) {
        return getHeaders(request);
    }

    @GetMapping("/aggregator/service")
    public Map<String, Object> service1(HttpServletRequest request) {
        return getHeaders(request);
    }

    @GetMapping("/aggregator/service1")
    public String service2() {
        return "aggregator1";
    }

    private Map<String, Object> getHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        return headers;
    }
}
