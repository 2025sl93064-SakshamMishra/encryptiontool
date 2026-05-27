package com.example.encryptiontool.dto;

import com.example.encryptiontool.model.AlgorithmType;
import lombok.Data;

@Data
public class TextEncryptRequest {
    private String text;
    private AlgorithmType algorithm;
}
