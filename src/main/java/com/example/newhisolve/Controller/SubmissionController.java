package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Request.SubmissionRequest;
import com.example.newhisolve.Service.AssignmentServiceImpl;
import com.example.newhisolve.Service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class SubmissionController {

    @Autowired
    private AssignmentServiceImpl assignmentService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @PostMapping("/api/submit")
    public Submission submitCode(@RequestBody SubmissionRequest submissionRequest) {
        Assignment assignment = assignmentService.getAssignmentById(submissionRequest.getAssignmentId());
        // 사용자 세션 check
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student;
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            student = userService.findByUsername(username);  // 사용자명을 통해 학생 정보 가져오기
        } else {
            throw new RuntimeException("User not authenticated");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setCode(submissionRequest.getCode());
        submission.setLanguage(submissionRequest.getLanguage());
        submission.setSubmittedAt(new Date());
        submission.setResult("Pass");
        submission.setStudent(student);

        return submissionRepository.save(submission);
    }
}
