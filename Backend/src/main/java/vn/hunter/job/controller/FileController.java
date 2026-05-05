package vn.hunter.job.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.http.HttpHeaders;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.hunter.job.domain.CVData;
import vn.hunter.job.domain.response.File.ResUploadFileDTO;
import vn.hunter.job.service.CvParserService;
import vn.hunter.job.service.ExcelService;
import vn.hunter.job.service.FileService;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.StorageException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import org.springframework.core.io.ByteArrayResource;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Autowired
    private CvParserService cvParserService;

    @Autowired
    private ExcelService excelService;
    @Value("${duck.upload-file.base-uri}")
    private String baseURI;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload a single file")
    public ResponseEntity<ResUploadFileDTO> uploadFIle(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file");
        }

        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.contains(extension);
        if (!isValid) {
            throw new StorageException("Invalid file extension. only allows " + allowedExtensions.toString());
        }
        this.fileService.createDirectory(baseURI + folder);

        String uploadFile = this.fileService.store(file, folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());
        System.out.println(">>> fileName = " + fileName);
        System.out.println(">>> folder = " + folder);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(@RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws StorageException, URISyntaxException, FileNotFoundException {
        System.out.println(">>> fileName = " + fileName);
        System.out.println(">>> folder = " + folder);
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder not found");
        }
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + fileName + " not found");
        }
        InputStreamResource resource = this.fileService.getResource(fileName, folder);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/files/cv-to-excel")
    @ApiMessage("Upload CV and Export to Excel")
    public ResponseEntity<Resource> uploadCvAndExportToExcel(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File empty");
        }
        String pdfText;
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            pdfText = stripper.getText(document);
        }
        CVData cv = cvParserService.parse(pdfText);
        ByteArrayResource resource = excelService.generateExcel(cv);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

}