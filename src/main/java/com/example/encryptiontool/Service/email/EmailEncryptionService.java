package com.example.encryptiontool.service.email;

import com.example.encryptiontool.model.AlgorithmType;
import com.example.encryptiontool.model.OperationStatus;
import com.example.encryptiontool.model.OperationType;
import com.example.encryptiontool.model.User;
import com.example.encryptiontool.service.encryption.EncryptionHistoryService;
import com.example.encryptiontool.service.encryption.EncryptionServiceRouter;
import com.example.encryptiontool.service.encryption.strategy.EncryptionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailEncryptionService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EncryptionServiceRouter router;

    @Autowired
    private EncryptionHistoryService historyService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String sendEncryptedEmail(User sender,
                                     String toEmail,
                                     String subject,
                                     String body,
                                     AlgorithmType algorithm) throws Exception {

        // Step 1 — encrypt the body
        EncryptionStrategy strategy = router.getStrategy(algorithm);
        String encryptedBody = strategy.encrypt(body);

        // Step 2 — build and send the email synchronously
        // (only returns success to caller after Gmail actually accepts it)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(buildEmailContent(encryptedBody, algorithm), false);

        mailSender.send(message);

        // Step 3 — log only after confirmed send
        historyService.saveHistory(
                sender, OperationType.ENCRYPT_EMAIL,
                algorithm, body, encryptedBody, OperationStatus.SUCCESS
        );

        return encryptedBody;
    }

    public String decryptEmailBody(User user,
                                   String encryptedText,
                                   AlgorithmType algorithm) throws Exception {

        EncryptionStrategy strategy = router.getStrategy(algorithm);
        String originalBody = strategy.decrypt(encryptedText);

        historyService.saveHistory(
                user, OperationType.DECRYPT_EMAIL,
                algorithm, encryptedText, originalBody, OperationStatus.SUCCESS
        );

        return originalBody;
    }

    private String buildEmailContent(String encryptedBody, AlgorithmType algorithm) {
        return "--- ENCRYPTED MESSAGE ---\n\n"
                + encryptedBody
                + "\n\n--- END OF ENCRYPTED MESSAGE ---\n"
                + "Algorithm: " + algorithm.getDisplayName() + "\n"
                + "To read this message, paste the encrypted text into the decryption tool.";
    }
}

