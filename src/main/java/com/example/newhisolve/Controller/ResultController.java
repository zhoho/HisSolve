package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/results")
public class ResultController {

    private final SubmissionService submissionService;
    private final AssignmentService assignmentService;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadResults(@RequestParam("assignmentId") Long assignmentId) throws IOException {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        List<Submission> submissions = submissionService.findByAssignmentId(assignmentId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Submissions");

        // Header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("P/F");
        header.createCell(3).setCellValue("Correct");
        header.createCell(4).setCellValue("Time");

        // Create styles for pass and fail
        CellStyle passStyle = workbook.createCellStyle();
        passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle failStyle = workbook.createCellStyle();
        failStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (Submission submission : submissions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(submission.getStudent().getUsername());
            row.createCell(1).setCellValue(submission.getStudent().getEmail());
            Cell pfCell = row.createCell(2);
            if (submission.getPass_count().equals(String.valueOf(assignment.getTestCases().size()))) {
                pfCell.setCellValue("성공");
                pfCell.setCellStyle(passStyle);
            } else {
                pfCell.setCellValue("실패");
                pfCell.setCellStyle(failStyle);
            }
            row.createCell(3).setCellValue(submission.getPass_count() + "/" + assignment.getTestCases().size());
            row.createCell(4).setCellValue(submission.getSubmittedAt().format(formatter));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] content = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "submissions.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}
