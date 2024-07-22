package com.example.newhisolve.Controller;

import com.example.newhisolve.dto.SubmissionDTO;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/saveCode")
    public ResponseEntity<SubmissionDTO> saveCode(@RequestBody SubmissionDTO submissionDTO) {
        Submission submission = submissionService.saveCode(submissionDTO);

        SubmissionDTO savedSubmissionDTO = new SubmissionDTO(
                submission.getAssignment().getId(),
                submission.getStudent().getId(),
                submission.getCode(),
                submission.getLanguage(),
                submission.getLastSavedDate(),
                submissionDTO.getTotalCount(),
                submissionDTO.getPassCount()
        );

        return ResponseEntity.ok(savedSubmissionDTO);
    }

    @GetMapping("/getSavedCode")
    public String getSavedCode(@RequestParam Long assignmentId, @RequestParam Long studentId) {
        Optional<Submission> submissionOptional = submissionService.getSavedCode(assignmentId, studentId);
        if (submissionOptional.isPresent()) {
            Submission submission = submissionOptional.get();
            SubmissionDTO submissionDTO = new SubmissionDTO(
                    submission.getAssignment().getId(),
                    submission.getStudent().getId(),
                    submission.getCode(),
                    submission.getLanguage(),
                    submission.getLastSavedDate(),
                    submission.getTotal_count(),
                    submission.getPass_count()
            );
            return submission.getCode();
        } else {
            return null;
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
}
