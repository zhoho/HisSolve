package com.example.newhisolve.Controller;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.ProblemService;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubmitController {

    private final ProblemService problemService;
    private final UserService userService;
    private final SubmissionService submissionService;

    @GetMapping("/submit")
    @PreAuthorize("hasRole('USER')")
    public String showSubmitPage(@RequestParam("problemId") Long problemId,
                                 @RequestParam("language") String language,
                                 Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("language", language);
        model.addAttribute("user", user);
        Problem problem = problemService.getProblemById(problemId);
        model.addAttribute("problem", problem);
        List<Submission> submissions = submissionService.findByProblemAndUser(problemId, user.getId());
        model.addAttribute("submissions", submissions);

        return "submit";
    }
}
