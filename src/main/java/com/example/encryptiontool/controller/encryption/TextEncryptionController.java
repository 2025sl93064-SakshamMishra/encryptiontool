package com.example.encryptiontool.controller.encryption;

import com.example.encryptiontool.dto.ApiResponse;
import com.example.encryptiontool.dto.TextDecryptRequest;
import com.example.encryptiontool.dto.TextEncryptRequest;
import com.example.encryptiontool.model.*;
import com.example.encryptiontool.repository.UserRepository;
import com.example.encryptiontool.service.encryption.EncryptionHistoryService;
import com.example.encryptiontool.service.encryption.EncryptionServiceRouter;
import com.example.encryptiontool.service.encryption.strategy.EncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TextEncryptionController {

    @Autowired
    private EncryptionServiceRouter router;

    @Autowired
    private EncryptionHistoryService historyService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/encrypt/text")
    public ResponseEntity<ApiResponse<Map<String, String>>> encryptText(
            @RequestBody TextEncryptRequest request) {

        if (request.getText() == null || request.getText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Text cannot be empty"));
        }

        AlgorithmType algorithm = request.getAlgorithm() != null
                ? request.getAlgorithm()
                : AlgorithmType.AES_256;

        User user = getLoggedInUser();

        try {
            EncryptionStrategy strategy = router.getStrategy(algorithm);
            String encrypted = strategy.encrypt(request.getText());

            historyService.saveHistory(
                    user,
                    OperationType.ENCRYPT_TEXT,
                    algorithm,
                    request.getText(),
                    encrypted,
                    OperationStatus.SUCCESS
            );

            return ResponseEntity.ok(ApiResponse.ok(
                    "Text encrypted successfully using " + algorithm.getDisplayName(),
                    Map.of("result", encrypted, "algorithm", algorithm.getDisplayName())
            ));

        } catch (Exception e) {
            historyService.saveHistory(user, OperationType.ENCRYPT_TEXT,
                    algorithm, request.getText(), null, OperationStatus.FAILED);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Encryption failed: " + e.getMessage()));
        }
    }

    @PostMapping("/decrypt/text")
    public ResponseEntity<ApiResponse<Map<String, String>>> decryptText(
            @RequestBody TextDecryptRequest request) {

        if (request.getEncryptedText() == null || request.getEncryptedText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Encrypted text cannot be empty"));
        }

        AlgorithmType algorithm = request.getAlgorithm() != null
                ? request.getAlgorithm()
                : AlgorithmType.AES_256;

        User user = getLoggedInUser();

        try {
            EncryptionStrategy strategy = router.getStrategy(algorithm);
            String decrypted = strategy.decrypt(request.getEncryptedText());

            historyService.saveHistory(
                    user,
                    OperationType.DECRYPT_TEXT,
                    algorithm,
                    request.getEncryptedText(),
                    decrypted,
                    OperationStatus.SUCCESS
            );

            return ResponseEntity.ok(ApiResponse.ok(
                    "Text decrypted successfully using " + algorithm.getDisplayName(),
                    Map.of("result", decrypted, "algorithm", algorithm.getDisplayName())
            ));

        } catch (Exception e) {
            historyService.saveHistory(user, OperationType.DECRYPT_TEXT,
                    algorithm, request.getEncryptedText(), null, OperationStatus.FAILED);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Decryption failed: invalid data or wrong algorithm"));
        }
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
