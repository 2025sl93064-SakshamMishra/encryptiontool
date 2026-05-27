package com.example.encryptiontool.service.encryption;

import com.example.encryptiontool.model.AlgorithmType;
import com.example.encryptiontool.service.encryption.strategy.AesEncryptionStrategy;
import com.example.encryptiontool.service.encryption.strategy.EncryptionStrategy;
import com.example.encryptiontool.service.encryption.strategy.RsaEncryptionStrategy;
import com.example.encryptiontool.service.encryption.strategy.TripleDesEncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyPair;

@Service
public class EncryptionServiceRouter {

    @Autowired
    @Qualifier("aes256Key")
    private SecretKey aes256Key;

    @Autowired
    @Qualifier("aes128Key")
    private SecretKey aes128Key;

    @Autowired
    @Qualifier("tripleDesKey")
    private SecretKey tripleDesKey;

    @Autowired
    @Qualifier("rsaKeyPair")
    private KeyPair rsaKeyPair;

    public EncryptionStrategy getStrategy(AlgorithmType algorithm) {
        return switch (algorithm) {
            case AES_128    -> new AesEncryptionStrategy(aes128Key);
            case AES_256    -> new AesEncryptionStrategy(aes256Key);
            case TRIPLE_DES -> new TripleDesEncryptionStrategy(tripleDesKey);
            case RSA        -> new RsaEncryptionStrategy(rsaKeyPair);
        };
    }
}
