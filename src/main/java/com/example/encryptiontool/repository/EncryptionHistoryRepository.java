package com.example.encryptiontool.repository;

import com.example.encryptiontool.model.EncryptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncryptionHistoryRepository extends JpaRepository<EncryptionHistory, Long> {

    List<EncryptionHistory> findByUserIdOrderByPerformedAtDesc(Long userId);
}
