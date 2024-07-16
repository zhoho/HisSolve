package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Request.SubmissionRequest;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
