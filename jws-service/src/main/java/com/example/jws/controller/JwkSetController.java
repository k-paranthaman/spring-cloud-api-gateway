package com.example.jws.controller;


import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
public class JwkSetController {

    private final TokenController tokenController;

    public JwkSetController(TokenController tokenController) {
        this.tokenController = tokenController;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) tokenController.getKeyPair().getPublic();
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID(tokenController.getKeyId())
                .build();
        JWKSet jwkSet = new JWKSet(jwk);
        return jwkSet.toJSONObject();
    }
}
