package com.example.encryptiontool.controller.email;

import com.example.encryptiontool.controller.BaseController;
import com.example.encryptiontool.dto.ApiResponse;
import com.example.encryptiontool.dto.EmailDecryptRequest;
import com.example.encryptiontool.dto.EmailEncryptRequest;
import com.example.encryptiontool.exception.AppException;
import com.example.encryptiontool.model.AlgorithmType;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.service.email.EmailEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController extends BaseController {

    @Autowired
    private EmailEncryptionService emailEncryptionService;

    @PostMapping("/send-encrypted")
    public ResponseEntity<ApiResponse<Void>> sendEncryptedEmail(
            @RequestBody EmailEncryptRequest request) {

        if (request.getToEmail() == null || request.getToEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Recipient email cannot be empty"));
        }

        if (request.getBody() == null || request.getBody().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email body cannot be empty"));
        }

        AlgorithmType algorithm = request.getAlgorithm() != null
                ? request.getAlgorithm()
                : AlgorithmType.AES_256;

        User sender = getLoggedInUser();

        try {
            emailEncryptionService.sendEncryptedEmail(
                    sender,
                    request.getToEmail(),
                    request.getSubject() != null ? request.getSubject() : "Encrypted Message",
                    request.getBody(),
                    algorithm
            );

            return ResponseEntity.ok(ApiResponse.ok(
                    "Encrypted email sent successfully to " + request.getToEmail()
                    + " using " + algorithm.getDisplayName(),
                    null
            ));

        } catch (Exception e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<ApiResponse<Map<String, String>>> decryptEmail(
            @RequestBody EmailDecryptRequest request) {

        if (request.getEncryptedText() == null || request.getEncryptedText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Encrypted text cannot be empty"));
        }

        AlgorithmType algorithm = request.getAlgorithm() != null
                ? request.getAlgorithm()
                : AlgorithmType.AES_256;

        User user = getLoggedInUser();

        try {
            String originalBody = emailEncryptionService.decryptEmailBody(
                    user, request.getEncryptedText(), algorithm
            );

            return ResponseEntity.ok(ApiResponse.ok(
                    "Email decrypted successfully using " + algorithm.getDisplayName(),
                    Map.of("originalMessage", originalBody, "algorithm", algorithm.getDisplayName())
            ));

        } catch (Exception e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Decryption failed: invalid text or wrong algorithm");
        }
    }
}
