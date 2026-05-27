package com.example.encryptiontool.service.export;

import com.example.encryptiontool.model.*;
import com.example.encryptiontool.repository.EncryptionHistoryRepository;
import com.example.encryptiontool.repository.FileRecordRepository;
import com.example.encryptiontool.service.encryption.EncryptionHistoryService;
import com.example.encryptiontool.service.encryption.EncryptionServiceRouter;
import com.example.encryptiontool.service.encryption.strategy.EncryptionStrategy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private EncryptionHistoryRepository historyRepository;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Autowired
    private EncryptionHistoryService historyService;

    @Autowired
    private EncryptionServiceRouter router;

    // ----------------------------------------------------------------
    // ENCRYPT: build 3-sheet Excel for this user → encrypt → return bytes
    // ----------------------------------------------------------------
    public byte[] buildAndEncryptExcel(User user) throws Exception {

        // Step 1 — build workbook in memory
        byte[] xlsxBytes = buildWorkbook(user);

        // Step 2 — Base64 encode xlsx bytes → String for AES (AES works on text)
        String base64Xlsx = Base64.getEncoder().encodeToString(xlsxBytes);

        // Step 3 — encrypt with AES-256
        EncryptionStrategy aes256 = router.getStrategy(AlgorithmType.AES_256);
        String encryptedContent = aes256.encrypt(base64Xlsx);

        // Step 4 — save FileRecord
        String encryptedFileName = "export_" + sanitizeEmail(user.getEmail()) + ".enc";
        FileRecord record = new FileRecord();
        record.setUser(user);
        record.setOriginalFileName("export_" + sanitizeEmail(user.getEmail()) + ".xlsx");
        record.setEncryptedFileName(encryptedFileName);
        record.setFileSizeBytes((long) xlsxBytes.length);
        record.setStatus(FileStatus.ENCRYPTED);
        fileRecordRepository.save(record);

        // Step 5 — log to encryption history
        historyService.saveHistory(
                user, OperationType.EXPORT_ENCRYPT, AlgorithmType.AES_256,
                record.getOriginalFileName(), encryptedFileName, OperationStatus.SUCCESS
        );

        return encryptedContent.getBytes("UTF-8");
    }

    // ----------------------------------------------------------------
    // DECRYPT: read .enc content → decrypt → Base64 decode → xlsx bytes
    // ----------------------------------------------------------------
    public byte[] decryptToExcel(User user, byte[] encFileBytes) throws Exception {

        String encryptedContent = new String(encFileBytes, "UTF-8");

        EncryptionStrategy aes256 = router.getStrategy(AlgorithmType.AES_256);
        String base64Xlsx = aes256.decrypt(encryptedContent);

        byte[] xlsxBytes = Base64.getDecoder().decode(base64Xlsx);

        historyService.saveHistory(
                user, OperationType.EXPORT_DECRYPT, AlgorithmType.AES_256,
                "export_" + sanitizeEmail(user.getEmail()) + ".enc",
                "export_" + sanitizeEmail(user.getEmail()) + "_decrypted.xlsx",
                OperationStatus.SUCCESS
        );

        return xlsxBytes;
    }

    // ----------------------------------------------------------------
    // Build the 3-sheet workbook for the logged-in user
    // ----------------------------------------------------------------
    private byte[] buildWorkbook(User user) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = buildHeaderStyle(workbook);

            buildProfileSheet(workbook, headerStyle, user);
            buildHistorySheet(workbook, headerStyle, user);
            buildFileRecordsSheet(workbook, headerStyle, user);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void buildProfileSheet(XSSFWorkbook workbook, CellStyle headerStyle, User user) {
        Sheet sheet = workbook.createSheet("Profile");

        String[] headers = {"ID", "Name", "Email", "Role", "Registered At"};
        writeHeaderRow(sheet, headerStyle, headers);

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(user.getId());
        row.createCell(1).setCellValue(user.getName());
        row.createCell(2).setCellValue(user.getEmail());
        row.createCell(3).setCellValue(user.getRole());
        row.createCell(4).setCellValue(
                user.getCreatedAt() != null ? user.getCreatedAt().format(FMT) : "N/A"
        );

        autoSizeColumns(sheet, headers.length);
    }

    private void buildHistorySheet(XSSFWorkbook workbook, CellStyle headerStyle, User user) {
        Sheet sheet = workbook.createSheet("Encryption History");

        String[] headers = {"ID", "Operation", "Algorithm", "Input", "Output", "Status", "Date"};
        writeHeaderRow(sheet, headerStyle, headers);

        List<EncryptionHistory> historyList =
                historyRepository.findByUserIdOrderByPerformedAtDesc(user.getId());

        int rowNum = 1;
        for (EncryptionHistory h : historyList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(h.getId());
            row.createCell(1).setCellValue(h.getOperationType() != null ? h.getOperationType().name() : "");
            row.createCell(2).setCellValue(h.getAlgorithmType() != null ? h.getAlgorithmType().getDisplayName() : "");
            row.createCell(3).setCellValue(h.getInputName() != null ? h.getInputName() : "");
            row.createCell(4).setCellValue(h.getOutputName() != null ? h.getOutputName() : "");
            row.createCell(5).setCellValue(h.getStatus() != null ? h.getStatus().name() : "");
            row.createCell(6).setCellValue(h.getPerformedAt() != null ? h.getPerformedAt().format(FMT) : "");
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void buildFileRecordsSheet(XSSFWorkbook workbook, CellStyle headerStyle, User user) {
        Sheet sheet = workbook.createSheet("File Records");

        String[] headers = {"ID", "Original File", "Encrypted File", "Size (bytes)", "Status", "Date"};
        writeHeaderRow(sheet, headerStyle, headers);

        List<FileRecord> records = fileRecordRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        int rowNum = 1;
        for (FileRecord f : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(f.getId());
            row.createCell(1).setCellValue(f.getOriginalFileName() != null ? f.getOriginalFileName() : "");
            row.createCell(2).setCellValue(f.getEncryptedFileName() != null ? f.getEncryptedFileName() : "");
            row.createCell(3).setCellValue(f.getFileSizeBytes() != null ? f.getFileSizeBytes() : 0);
            row.createCell(4).setCellValue(f.getStatus() != null ? f.getStatus().name() : "");
            row.createCell(5).setCellValue(f.getCreatedAt() != null ? f.getCreatedAt().format(FMT) : "");
        }

        autoSizeColumns(sheet, headers.length);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private void writeHeaderRow(Sheet sheet, CellStyle style, String[] headers) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle buildHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void autoSizeColumns(Sheet sheet, int colCount) {
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String sanitizeEmail(String email) {
        return email.replace("@", "_").replace(".", "_");
    }
}
