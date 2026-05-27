package com.example.encryptiontool.repository;

import com.example.encryptiontool.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

    List<FileRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
