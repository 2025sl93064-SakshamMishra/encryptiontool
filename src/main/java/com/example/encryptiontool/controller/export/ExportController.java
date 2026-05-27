package com.example.encryptiontool.controller.export;

import com.example.encryptiontool.dto.ApiResponse;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.repository.UserRepository;
import com.example.encryptiontool.service.export.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExcelExportService exportService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/export/encrypt
    // Exports logged-in user's data as encrypted .enc file download
    @GetMapping("/encrypt")
    public ResponseEntity<?> exportAndEncrypt() {
        User user = getLoggedInUser();

        try {
            byte[] encryptedBytes = exportService.buildAndEncryptExcel(user);
            String fileName = "export_" + sanitizeEmail(user.getEmail()) + ".enc";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(encryptedBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Export failed: " + e.getMessage()));
        }
    }

    // POST /api/export/decrypt
    // Accepts .enc file upload, returns decrypted .xlsx file download
    @PostMapping("/decrypt")
    public ResponseEntity<?> decryptExport(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No file uploaded"));
        }

        User user = getLoggedInUser();

        try {
            byte[] xlsxBytes = exportService.decryptToExcel(user, file.getBytes());
            String fileName = "export_" + sanitizeEmail(user.getEmail()) + "_decrypted.xlsx";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(xlsxBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Decryption failed: invalid file or corrupted data"));
        }
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private String sanitizeEmail(String email) {
        return email.replace("@", "_").replace(".", "_");
    }
}
