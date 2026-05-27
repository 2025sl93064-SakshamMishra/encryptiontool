package com.example.encryptiontool.service.encryption.strategy;

public interface EncryptionStrategy {
    String encrypt(String plainText) throws Exception;
    String decrypt(String encryptedText) throws Exception;
}
