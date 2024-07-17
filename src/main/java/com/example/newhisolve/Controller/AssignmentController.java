package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/assignment/create")
    public String showCreateAssignmentForm(@RequestParam Long courseId, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }
        Course course = courseService.findById(courseId);
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("course", course);
        return "create_assignment"; // Thymeleaf 템플릿 이름
    }

    @PostMapping("/assignment/create")
    public String createAssignment(@ModelAttribute Assignment assignment, @RequestParam Long courseId, @RequestParam List<String> inputs, @RequestParam List<String> outputs, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }

        // Add test cases to the assignment
        List<TestCase> testCases = new ArrayList<>();
        StringBuilder descriptionWithTestCases = new StringBuilder(assignment.getDescription()).append("\n\n --- \n");

        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);

            descriptionWithTestCases.append("\n#### Test Case ").append(i + 1).append(":\n");
            descriptionWithTestCases.append("<div style=\"display: flex;\">\n");
            descriptionWithTestCases.append("  <div style=\"flex: 1; padding: 10px; border: 1px solid #ccc; margin-right: 10px;\">\n");
            descriptionWithTestCases.append("    <strong>Input:</strong>\n");
            descriptionWithTestCases.append("    <pre>").append(inputs.get(i)).append("</pre>\n");
            descriptionWithTestCases.append("  </div>\n");
            descriptionWithTestCases.append("  <div style=\"flex: 1; padding: 10px; border: 1px solid #ccc;\">\n");
            descriptionWithTestCases.append("    <strong>Expected Output:</strong>\n");
            descriptionWithTestCases.append("    <pre>").append(outputs.get(i)).append("</pre>\n");
            descriptionWithTestCases.append("  </div>\n");
            descriptionWithTestCases.append("</div>\n");
        }

        assignment.setTestCases(testCases);
        assignment.setDescription(descriptionWithTestCases.toString());

        assignmentService.createAssignment(assignment, courseId);

        // Log saved assignment and test cases for debugging
        System.out.println("Saved Assignment: " + assignment.getId());
        for (TestCase testCase : assignment.getTestCases()) {
            System.out.println("Test Case - Input: " + testCase.getInput() + ", Expected Output: " + testCase.getExpectedOutput());
        }

        return "redirect:/course/" + courseId;
    }

    @GetMapping("/assignment/{id}")
    public String SolveAssignment(@PathVariable Long id, Principal principal) {
        Assignment assignment = assignmentService.findById(id);
        Course course = assignment.getCourse();
        return "redirect:/index?assignmentId=" + id + "&language=" + course.getLanguage();
    }

    @GetMapping("/professor_assignment/{id}")
    public String viewAssignment(@PathVariable Long id, Model model, Principal principal) {
        Assignment assignment = assignmentService.findById(id);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("assignment", assignment);
        model.addAttribute("user", user);
        model.addAttribute("submissions", assignmentService.findSubmissionsByAssignment(assignment));
        return "assignment_view";
    }
}
