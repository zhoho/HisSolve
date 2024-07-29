package com.example.newhisolve.Controller;
import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.AssignmentService;
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
public class IndexController {

    private final AssignmentService assignmentService;
    private final UserService userService;
    private final SubmissionService submissionService;

    @GetMapping("/index")
    @PreAuthorize("hasRole('STUDENT')")
    public String showIndexPage(@RequestParam("assignmentId") Long assignmentId,
                                @RequestParam("language") String language,
                                Model model) {
        User student = userService.getCurrentUser();
        model.addAttribute("language", language);
        model.addAttribute("student", student);
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        model.addAttribute("assignment", assignment);
        List<Submission> submissions = submissionService.findByAssignmentAndStudent(assignmentId, student.getId());
        model.addAttribute("submissions", submissions);

        return "index";
    }
}
