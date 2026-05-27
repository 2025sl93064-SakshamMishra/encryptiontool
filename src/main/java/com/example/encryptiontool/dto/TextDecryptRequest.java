package com.example.encryptiontool.dto;

import com.example.encryptiontool.model.AlgorithmType;
import lombok.Data;

@Data
public class TextDecryptRequest {
    private String encryptedText;
    private AlgorithmType algorithm;
}
