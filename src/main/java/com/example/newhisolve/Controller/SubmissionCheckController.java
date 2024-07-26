package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class SubmissionCheckController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @GetMapping("/submission_view/{assignmentId}")
    public String checkSubmission(@PathVariable Long assignmentId, Model model, Principal principal) {
        List<Submission> submissions = submissionService.findByAssignmentId(assignmentId);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("submissions", submissions);
        model.addAttribute("user", user);
        System.out.println("Returning submission_view template with submissions: " + submissions.size());
        System.out.println("Submissions: " + submissions);
        System.out.println("User: " + user.getUsername());
        return "submission_view";
    }

    @PostMapping("/submission/delete/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmissionById(id);
        return ResponseEntity.ok().build();
    }

}
