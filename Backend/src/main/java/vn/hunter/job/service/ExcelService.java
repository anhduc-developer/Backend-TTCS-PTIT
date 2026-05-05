package vn.hunter.job.service;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import vn.hunter.job.domain.CVData;

@Service
public class ExcelService {
    public ByteArrayResource generateExcel(CVData cv) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("CV");
            Row header = sheet.createRow(0);
            header.createCell(1).setCellValue("Email");
            header.createCell(2).setCellValue("Phone");
            header.createCell(3).setCellValue("Location");
            header.createCell(4).setCellValue("Skills");

            Row row = sheet.createRow(1);
            row.createCell(1).setCellValue(cv.email);
            row.createCell(2).setCellValue(cv.phone);
            row.createCell(3).setCellValue(cv.location);
            row.createCell(4).setCellValue(String.join(", ", cv.skills));
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel: " + e.getMessage());
        }
    }
}