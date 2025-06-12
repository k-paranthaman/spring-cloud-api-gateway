package com.example.gateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app-jws")
public record JwsProperties(String jwkUrl,
        int connectTimeout,
        int readTimeout,
        long jwkCacheLifespan,
        long jwkRefreshTime) {

}
