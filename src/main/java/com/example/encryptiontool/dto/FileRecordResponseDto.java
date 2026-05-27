package com.example.encryptiontool.dto;

import com.example.encryptiontool.model.FileRecord;
import lombok.Data;

@Data
public class FileRecordResponseDto {

    private Long id;
    private String originalFile;
    private String encryptedFile;
    private Long sizeBytes;
    private String status;
    private String createdAt;

    private static final java.time.format.DateTimeFormatter FMT =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static FileRecordResponseDto from(FileRecord f) {
        FileRecordResponseDto dto = new FileRecordResponseDto();
        dto.id            = f.getId();
        dto.originalFile  = f.getOriginalFileName()  != null ? f.getOriginalFileName()             : "";
        dto.encryptedFile = f.getEncryptedFileName() != null ? f.getEncryptedFileName()            : "";
        dto.sizeBytes     = f.getFileSizeBytes()     != null ? f.getFileSizeBytes()                : 0L;
        dto.status        = f.getStatus()            != null ? f.getStatus().name()                : "";
        dto.createdAt     = f.getCreatedAt()         != null ? f.getCreatedAt().format(FMT)        : "";
        return dto;
    }
}
