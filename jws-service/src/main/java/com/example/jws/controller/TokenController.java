package com.example.jws.controller;


import com.example.jws.producer.GenerateToken;
import org.springframework.web.bind.annotation.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final KeyPair keyPair;
    private final String keyId = "my-key-id";

    public TokenController() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        this.keyPair = keyGen.generateKeyPair();
    }

    @GetMapping("/token")
    public String generateToken() throws Exception {
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return GenerateToken.generateToken(privateKey, keyId);
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getKeyId() {
        return keyId;
    }
}
