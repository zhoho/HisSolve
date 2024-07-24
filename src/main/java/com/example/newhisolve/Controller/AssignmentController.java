package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
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

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/assignment/create")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String showCreateAssignmentForm(@RequestParam Long courseId, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }
        Course course = courseService.findById(courseId);
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("course", course);
        return "create_assignment";
    }

    @PostMapping("/assignment/create")
    public String createAssignment(@ModelAttribute Assignment assignment,
                                   @RequestParam Long courseId,
                                   @RequestParam List<String> inputs,
                                   @RequestParam List<String> outputs,
                                   Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }

        List<TestCase> testCases = new ArrayList<>();
        StringBuilder descriptionWithTestCases = new StringBuilder(assignment.getDescription()).append("\n\n --- \n");

        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);

            descriptionWithTestCases.append("\n#### 예제 입력 ").append(i + 1).append(":\n");
            descriptionWithTestCases.append("<div style=\"display: flex;\">\n");
            descriptionWithTestCases.append("  <div style=\"flex: 1; padding: 10px; border: 1px solid #ccc; margin-right: 10px;\">\n");
            descriptionWithTestCases.append("    <pre>").append(inputs.get(i)).append("</pre>\n");
            descriptionWithTestCases.append("  </div>\n");
            descriptionWithTestCases.append("\n#### 예제 출력 ").append(i + 1).append(":\n");
            descriptionWithTestCases.append("  <div style=\"flex: 1; padding: 10px; border: 1px solid #ccc;\">\n");
            descriptionWithTestCases.append("    <pre>").append(outputs.get(i)).append("</pre>\n");
            descriptionWithTestCases.append("  </div>\n");
            descriptionWithTestCases.append("</div>\n");
        }

        assignment.setTestcaseCount(String.valueOf(inputs.size()));
        assignment.setTestCases(testCases);
        assignment.setDescription(descriptionWithTestCases.toString());

        assignmentService.createAssignment(assignment, courseId);

        for (TestCase testCase : assignment.getTestCases()) {
            System.out.println("Test Case - Input: " + testCase.getInput() + ", Expected Output: " + testCase.getExpectedOutput());
        }

        return "redirect:/professor_course/" + courseId;
    }



    @GetMapping("/assignment/{id}")
    public String SolveAssignment(@PathVariable Long id, Principal principal) {
        Assignment assignment = assignmentService.findById(id);
        Course course = assignment.getCourse();
        return "redirect:/index?assignmentId=" + id + "&language=" + course.getLanguage();
    }

    @GetMapping("/professor_assignment/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String viewAssignment(@PathVariable Long id, Model model, Principal principal) {
        Assignment assignment = assignmentService.findById(id);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("assignment", assignment);
        model.addAttribute("user", user);
        model.addAttribute("submissions", assignmentService.findSubmissionsByAssignment(assignment));
        return "assignment_view";
    }

    @PostMapping("/assignment/delete/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String deleteAssignment(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        //교수 처리
        if(!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }
        Assignment assignment = assignmentService.findById(id);
        Long courseId = assignment.getCourse().getId();
        submissionService.deleteSubmissionsByAssignmentId(id);
        assignmentService.deleteAssignmentById(id);
        return "redirect:/professor_course/" + courseId;
    }

    @GetMapping("/assignment/edit/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String showEditAssignmentForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getRole().equals("PROFESSOR")) {
            Assignment assignment = assignmentService.findById(id);
            model.addAttribute("assignment", assignment);
            model.addAttribute("course", assignment.getCourse());
            model.addAttribute("testCases", assignment.getTestCases());
            return "edit_assignment";  // 새로 만들 수정 페이지 템플릿
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/assignment/update")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String updateAssignment(@ModelAttribute Assignment assignment, @RequestParam Long courseId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getRole().equals("PROFESSOR")) {
//            List<TestCase> testCases = new ArrayList<>();
//            for (int i = 0; i < inputs.size(); i++) {
//                TestCase testCase = new TestCase();
//                testCase.setInput(inputs.get(i));
//                testCase.setExpectedOutput(outputs.get(i));
//                testCases.add(testCase);
//            }
//            assignment.setTestCases(testCases);
            assignmentService.updateAssignment(assignment, courseId);
        }
        return "redirect:/professor_course/" + courseId;
    }

    @GetMapping("/professor_assignment_detail/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String detailAsignment(@PathVariable Long id, Principal principal, Model model) {
        Assignment assignment = assignmentService.findById(id);
        User user = userService.findByUsername(principal.getName());
        Course course = assignment.getCourse();
        model.addAttribute("assignment", assignment);
        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("submissions", assignmentService.findSubmissionsByAssignment(assignment));
//        return "redirect:/professor_assignment_detail/" + course.getId();
        return "professor_assignment_detail";

    }
}