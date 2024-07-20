package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Request.SubmissionRequest;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;


    @PostMapping("/api/submit")
    public ResponseEntity<?> submitCode(@RequestBody SubmissionRequest submissionRequest) {
        try {
            Assignment assignment = assignmentService.getAssignmentById(submissionRequest.getAssignmentId());

            // 사용자 세션 check
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User student;
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                student = userService.findByUsername(username);
            } else {
                throw new RuntimeException("User not authenticated");
            }

            // 제출 데이터 저장
            Submission savedSubmission = submissionService.saveSubmission(assignment, student, submissionRequest.getCode(), submissionRequest.getLanguage(), submissionRequest.getTotalTestCases(), submissionRequest.getPassedTestCases());

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

    @PostMapping("/api/saveCode")
    public ResponseEntity<Map<String, Object>> saveCode(@RequestBody Map<String, Object> request, Authentication authentication) {
        Long assignmentId = Long.parseLong(request.get("assignmentId").toString());
        Long studentId = Long.parseLong(request.get("studentId").toString());
        String code = request.get("code").toString();
        String language = request.get("language").toString();

        Submission submission = submissionService.saveCode(assignmentId, studentId, code, language);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Code saved successfully");
        response.put("lastSavedDate", submission.getLastSavedDate());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/getSavedCode")
    public Optional<Submission> getSavedCode(@RequestParam Long assignmentId, @RequestParam Long studentId) {
        return submissionService.getSavedCode(assignmentId, studentId);
    }

    static class SaveCodeRequest {
        private Long assignmentId;
        private Long studentId;
        private String code;
        private String language;

        // getters and setters
        public Long getAssignmentId() {
            return assignmentId;
        }

        public void setAssignmentId(Long assignmentId) {
            this.assignmentId = assignmentId;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

}
