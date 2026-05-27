package com.example.encryptiontool.service.report;

import com.example.encryptiontool.dto.FileRecordResponseDto;
import com.example.encryptiontool.dto.HistoryResponseDto;
import com.example.encryptiontool.dto.ReportSummaryDto;
import com.example.encryptiontool.model.EncryptionHistory;
import com.example.encryptiontool.model.FileRecord;
import com.example.encryptiontool.model.OperationType;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.repository.EncryptionHistoryRepository;
import com.example.encryptiontool.repository.FileRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Set<OperationType> ENCRYPT_OPS = Set.of(
            OperationType.ENCRYPT_TEXT,
            OperationType.ENCRYPT_FILE,
            OperationType.ENCRYPT_EMAIL,
            OperationType.EXPORT_ENCRYPT
    );

    private static final Set<OperationType> DECRYPT_OPS = Set.of(
            OperationType.DECRYPT_TEXT,
            OperationType.DECRYPT_FILE,
            OperationType.DECRYPT_EMAIL,
            OperationType.EXPORT_DECRYPT
    );

    private static final Set<OperationType> EMAIL_OPS = Set.of(
            OperationType.ENCRYPT_EMAIL,
            OperationType.DECRYPT_EMAIL
    );

    private static final Set<OperationType> EXPORT_OPS = Set.of(
            OperationType.EXPORT_ENCRYPT,
            OperationType.EXPORT_DECRYPT
    );

    @Autowired
    private EncryptionHistoryRepository historyRepository;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    public ReportSummaryDto getSummary(User user) {
        List<EncryptionHistory> all = historyRepository.findByUserIdOrderByPerformedAtDesc(user.getId());

        long encryptions = all.stream()
                .filter(h -> h.getOperationType() != null && ENCRYPT_OPS.contains(h.getOperationType()))
                .count();

        long decryptions = all.stream()
                .filter(h -> h.getOperationType() != null && DECRYPT_OPS.contains(h.getOperationType()))
                .count();

        long emailsSent = all.stream()
                .filter(h -> h.getOperationType() == OperationType.ENCRYPT_EMAIL)
                .count();

        long exports = all.stream()
                .filter(h -> h.getOperationType() != null && EXPORT_OPS.contains(h.getOperationType()))
                .count();

        return new ReportSummaryDto(all.size(), encryptions, decryptions, emailsSent, exports);
    }

    public List<HistoryResponseDto> getHistory(User user) {
        return historyRepository.findByUserIdOrderByPerformedAtDesc(user.getId())
                .stream()
                .map(HistoryResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<FileRecordResponseDto> getFileRecords(User user) {
        return fileRecordRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(FileRecordResponseDto::from)
                .collect(Collectors.toList());
    }
}
