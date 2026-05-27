package com.example.encryptiontool.controller.report;

import com.example.encryptiontool.dto.ApiResponse;
import com.example.encryptiontool.dto.FileRecordResponseDto;
import com.example.encryptiontool.dto.HistoryResponseDto;
import com.example.encryptiontool.dto.ReportSummaryDto;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.repository.UserRepository;
import com.example.encryptiontool.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/reports/summary
    // Returns total counts: operations, encryptions, decryptions, emails sent, exports
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        User user = getLoggedInUser();
        ReportSummaryDto summary = reportService.getSummary(user);
        return ResponseEntity.ok(ApiResponse.ok("Report summary fetched", summary));
    }

    // GET /api/reports/history
    // Returns full encryption/decryption history for the logged-in user
    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        User user = getLoggedInUser();
        List<HistoryResponseDto> history = reportService.getHistory(user);
        return ResponseEntity.ok(ApiResponse.ok("History fetched", history));
    }

    // GET /api/reports/files
    // Returns all file operation records for the logged-in user
    @GetMapping("/files")
    public ResponseEntity<?> getFileRecords() {
        User user = getLoggedInUser();
        List<FileRecordResponseDto> files = reportService.getFileRecords(user);
        return ResponseEntity.ok(ApiResponse.ok("File records fetched", files));
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
