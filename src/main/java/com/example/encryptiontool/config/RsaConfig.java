package com.example.encryptiontool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

@Configuration
public class RsaConfig {

    // Generates a fresh 2048-bit RSA keypair every time the app starts.
    // Both encrypt and decrypt use this same in-memory keypair.
    // Data encrypted in one session cannot be decrypted after a restart —
    // which is acceptable for this project's scope.
    @Bean(name = "rsaKeyPair")
    public KeyPair rsaKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
}
