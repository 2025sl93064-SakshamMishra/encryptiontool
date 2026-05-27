package com.example.encryptiontool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDto {

    private long totalOperations;
    private long encryptions;
    private long decryptions;
    private long emailsSent;
    private long exports;
}
