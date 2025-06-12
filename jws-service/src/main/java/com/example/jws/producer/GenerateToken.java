package com.example.jws.producer;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

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
                .claim("userId", UUID.randomUUID().toString())
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

}
