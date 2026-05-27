package com.example.encryptiontool.dto;

import com.example.encryptiontool.model.AlgorithmType;
import lombok.Data;

@Data
public class EmailEncryptRequest {

    private String toEmail;
    private String subject;
    private String body;
    private AlgorithmType algorithm;
}
