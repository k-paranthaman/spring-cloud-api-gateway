package com.example.jws.producer;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.UUID;

public class GenerateToken {


    public static String generateToken(RSAPrivateKey privateKey, String keyId) throws Exception {
        // 1. Create RSA signer
        JWSSigner signer = new RSASSASigner(privateKey);

        // 2. Prepare JWT with claims
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("user123")
                .issuer("your-app")
                .claim("uamid", UUID.randomUUID().toString())
                .claim("deviceId", "device-456")
                .expirationTime(new Date(new Date().getTime() + 60 * 60 * 1000)) // 1 hour expiry
                .build();

        // 3. Create JWS header with key ID
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(keyId)
                .build();

        // 4. Create and sign the JWT
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);

        // 5. Serialize to compact form
        return signedJWT.serialize();
    }

    public void getKey() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Call the generateToken method
        String jwt = null;
        try {
            jwt = GenerateToken.generateToken(privateKey, "my-key-id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(jwt);
    }
}
