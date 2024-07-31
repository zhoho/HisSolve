package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SubmissionDetailController {

    private final SubmissionService submissionService;

    @GetMapping("/submission_detail/{id}")
    public String getSubmissionDetail(@PathVariable Long id, @RequestParam String language, Model model) {
        Optional<Submission> submissionOptional = submissionService.getSubmission(id);
        if (submissionOptional.isPresent()) {
            Submission submission = submissionOptional.get();
            model.addAttribute("submissionCode", submission.getCode());
            model.addAttribute("submissionLanguage", language);
            model.addAttribute("assignment", submission.getAssignment());
            return "submission_detail";
        }
        return "error";
    }
}
