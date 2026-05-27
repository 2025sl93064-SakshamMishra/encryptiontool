package com.example.encryptiontool.dto;

import com.example.encryptiontool.model.AlgorithmType;
import lombok.Data;

@Data
public class EmailDecryptRequest {
    private String encryptedText;
    private AlgorithmType algorithm;
}
