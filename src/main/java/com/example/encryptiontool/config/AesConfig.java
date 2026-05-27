package com.example.encryptiontool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class AesConfig {

    @Value("${aes.secret-key}")
    private String aesSecretKey;

    // AES-256 key — uses full 32-byte key
    @Bean(name = "aes256Key")
    public SecretKey aes256Key() {
        byte[] keyBytes = aesSecretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // AES-128 key — uses first 16 bytes of the same key
    @Bean(name = "aes128Key")
    public SecretKey aes128Key() {
        byte[] fullKey = aesSecretKey.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = new byte[16];
        System.arraycopy(fullKey, 0, keyBytes, 0, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // 3DES key — uses first 24 bytes
    @Bean(name = "tripleDesKey")
    public SecretKey tripleDesKey() {
        byte[] fullKey = aesSecretKey.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = new byte[24];
        System.arraycopy(fullKey, 0, keyBytes, 0, 24);
        return new SecretKeySpec(keyBytes, "DESede");
    }
}
