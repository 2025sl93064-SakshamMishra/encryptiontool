package com.example.encryptiontool.dto;

import com.example.encryptiontool.model.EncryptionHistory;
import lombok.Data;

@Data
public class HistoryResponseDto {

    private Long id;
    private String operation;
    private String algorithm;
    private String input;
    private String output;
    private String status;
    private String performedAt;

    private static final java.time.format.DateTimeFormatter FMT =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HistoryResponseDto from(EncryptionHistory h) {
        HistoryResponseDto dto = new HistoryResponseDto();
        dto.id = h.getId();
        dto.operation  = h.getOperationType()  != null ? h.getOperationType().name()              : "";
        dto.algorithm  = h.getAlgorithmType()  != null ? h.getAlgorithmType().getDisplayName()    : "";
        dto.input      = h.getInputName()      != null ? h.getInputName()                         : "";
        dto.output     = h.getOutputName()     != null ? h.getOutputName()                        : "";
        dto.status     = h.getStatus()         != null ? h.getStatus().name()                     : "";
        dto.performedAt = h.getPerformedAt()   != null ? h.getPerformedAt().format(FMT)           : "";
        return dto;
    }
}
