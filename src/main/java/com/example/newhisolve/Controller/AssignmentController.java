package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/assignment/create")
    public String showCreateAssignmentForm(@RequestParam Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("course", course);
        return "create_assignment"; // Thymeleaf 템플릿 이름
    }

    @PostMapping("/assignment/create")
    public String createAssignment(@ModelAttribute Assignment assignment, @RequestParam Long courseId, Principal principal) {
        assignmentService.createAssignment(assignment, courseId);
        return "redirect:/course/" + courseId;
    }

    @GetMapping("/assignment/{id}")
    public String viewAssignment(@PathVariable Long id, Model model, Principal principal) {
        Assignment assignment = assignmentService.findById(id);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("assignment", assignment);
        model.addAttribute("user", user);
        if (user.getRole().equals("PROFESSOR")) {
            model.addAttribute("submissions", assignmentService.findSubmissionsByAssignment(assignment));
        }
        return "assignment_view";
    }

    @PostMapping("/assignment/submit")
    public String submitAssignment(@RequestParam Long assignmentId, @RequestParam String code, Principal principal) {
        assignmentService.submitAssignment(assignmentId, code, principal.getName());
        return "redirect:/assignment/view/" + assignmentId;
    }
}
