package com.example.encryptiontool.service.encryption.strategy;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.util.Base64;

public class RsaEncryptionStrategy implements EncryptionStrategy {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    // RSA-2048 with PKCS1 padding can encrypt max 245 bytes (~245 chars)
    private static final int MAX_CHUNK_SIZE = 245;
    // RSA-2048 always produces 256-byte output blocks
    private static final int ENCRYPTED_CHUNK_SIZE = 256;
    private static final String CHUNK_SEPARATOR = "|";

    private final KeyPair keyPair;

    public RsaEncryptionStrategy(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @Override
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

        byte[] inputBytes = plainText.getBytes("UTF-8");
        StringBuilder result = new StringBuilder();

        // Split input into 245-byte chunks to handle text longer than RSA block size
        int offset = 0;
        while (offset < inputBytes.length) {
            int chunkSize = Math.min(MAX_CHUNK_SIZE, inputBytes.length - offset);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(inputBytes, offset, chunk, 0, chunkSize);

            byte[] encryptedChunk = cipher.doFinal(chunk);

            if (result.length() > 0) result.append(CHUNK_SEPARATOR);
            result.append(Base64.getEncoder().encodeToString(encryptedChunk));

            offset += chunkSize;
        }

        return result.toString();
    }

    @Override
    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        String[] chunks = encryptedText.split("\\" + CHUNK_SEPARATOR);
        StringBuilder result = new StringBuilder();

        for (String chunk : chunks) {
            byte[] encryptedChunk = Base64.getDecoder().decode(chunk);
            byte[] decryptedChunk = cipher.doFinal(encryptedChunk);
            result.append(new String(decryptedChunk, "UTF-8"));
        }

        return result.toString();
    }
}
