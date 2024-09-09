package com.example.newhisolve.Controller;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.dto.SubmissionDTO;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.SavedCode; // 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/saveCode")
    public ResponseEntity<?> saveCode(@RequestBody SubmissionDTO submissionDTO) {
        try {
            SavedCode savedCode = submissionService.saveCode(submissionDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("lastSavedDate", savedCode.getLastSavedDate());
            response.put("message", "Code saved successfully!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/getSavedCode")
    @ResponseBody
    public ResponseEntity<String> getSavedCode(@RequestParam Long assignmentId, @RequestParam Long studentId) {
        Optional<SavedCode> savedCodeOptional = submissionService.getSavedCode(assignmentId, studentId);
        if (savedCodeOptional.isPresent()) {
            SavedCode savedCode = savedCodeOptional.get();
            return ResponseEntity.ok(savedCode.getCode());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No saved code found");
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmissionDTO submissionDTO) {
        try {
            Submission savedSubmission = submissionService.saveSubmission(submissionDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("submissionId", savedSubmission.getId());
            response.put("message", "Submission successful!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/gradingTestcaseCount")
    public ResponseEntity<Integer> getGradingTestcaseCount(@RequestParam Long assignmentId) {
        try {
            int count = Integer.parseInt(submissionService.getGradingTestcaseCount(assignmentId));
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }
}
