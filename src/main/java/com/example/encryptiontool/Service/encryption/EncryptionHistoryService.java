package com.example.encryptiontool.service.encryption;

import com.example.encryptiontool.model.*;
import com.example.encryptiontool.repository.EncryptionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncryptionHistoryService {

    @Autowired
    private EncryptionHistoryRepository repository;

    public void saveHistory(User user,
                            OperationType operationType,
                            AlgorithmType algorithmType,
                            String inputName,
                            String outputName,
                            OperationStatus status) {

        EncryptionHistory history = new EncryptionHistory();
        history.setUser(user);
        history.setOperationType(operationType);
        history.setAlgorithmType(algorithmType);
        // Store only first 100 chars to keep the record concise
        history.setInputName(truncate(inputName, 100));
        history.setOutputName(truncate(outputName, 100));
        history.setStatus(status);

        repository.save(history);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "...";
    }
}
