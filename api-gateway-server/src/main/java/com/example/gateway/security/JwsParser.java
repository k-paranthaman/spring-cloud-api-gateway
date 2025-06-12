package com.example.gateway.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwsParser {

    private final DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

    private final JwsProperties jwsProperties;

    public JWTClaimsSet parse(final String authUserHeader) throws ParseException, JOSEException, BadJOSEException {
        return jwtProcessor.process(authUserHeader, null);
    }

    @PostConstruct
    void initializeProcessor() {
        log.info("Initializing JWT processor with JWK URL: {}", jwsProperties.jwkUrl());
        final DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(
                jwsProperties.connectTimeout(), jwsProperties.readTimeout());
        final URL jwkUrl;
        try {
            jwkUrl = new URL(jwsProperties.jwkUrl());
        } catch (MalformedURLException exception) {
            log.error("Invalid JWK URL: {}", jwsProperties.jwkUrl(), exception);
            throw new IllegalStateException("Unable to access remote JWK server endpoint", exception);
        }

        final JWSKeySelector<SecurityContext> keySelector = getSecurityContextJWSKeySelector(jwkUrl, resourceRetriever);

        jwtProcessor.setJWSKeySelector(keySelector);
        log.info("JWT processor initialized successfully.");
    }

    private JWSKeySelector<SecurityContext> getSecurityContextJWSKeySelector(URL jwkUrl, DefaultResourceRetriever resourceRetriever) {

        JWKSource<SecurityContext> keySource = JWKSourceBuilder.create(jwkUrl, resourceRetriever)
                .cache(jwsProperties.jwkCacheLifespan(),
                        jwsProperties.jwkRefreshTime())
                .build();

        return new JWSVerificationKeySelector<>(
                JWSAlgorithm.RS256,
                keySource
        );
    }


}
