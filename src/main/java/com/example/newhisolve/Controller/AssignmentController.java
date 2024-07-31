package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserService userService;
    private final CourseService courseService;
    private final SubmissionService submissionService;

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
    @PreAuthorize("hasRole('PROFESSOR')")
    public String createAssignment(@ModelAttribute Assignment assignment,
                                   @RequestParam Long courseId,
                                   @RequestParam List<String> inputs,
                                   @RequestParam List<String> outputs,
                                   @RequestParam("dueDate") String dueDate,
                                   Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        assignment.setDueDate(localDateTime);

        List<TestCase> testCases = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);
        }

        assignment.setTestcaseCount(String.valueOf(inputs.size()));
        assignment.setTestCases(testCases);

        assignmentService.createAssignment(assignment, courseId);

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
        model.addAttribute("submissions", submissionService.findSubmissionsByAssignment(assignment));
        return "assignment_view";
    }

    @PostMapping("/assignment/delete/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String deleteAssignment(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());

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
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }
        Assignment assignment = assignmentService.findById(id);
        model.addAttribute("assignment", assignment);
        model.addAttribute("course", assignment.getCourse());
        model.addAttribute("testCases", assignment.getTestCases());
        return "edit_assignment";
    }


    @PostMapping("/assignment/update")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String updateAssignment(@ModelAttribute Assignment assignment,
                                   @RequestParam Long courseId,
                                   @RequestParam List<String> inputs,
                                   @RequestParam List<String> outputs,
                                   @RequestParam("dueDate") String dueDate,
                                   Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul")).withZoneSameInstant(ZoneId.of("UTC"));
        assignment.setDueDate(zonedDateTime.toLocalDateTime());
        assignment.setLastModifiedDate(LocalDateTime.now());

        List<TestCase> testCases = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);
        }

        assignment.setTestcaseCount(String.valueOf(inputs.size()));
        assignment.setTestCases(testCases);

        assignmentService.updateAssignment(assignment, courseId);

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
        model.addAttribute("submissions", submissionService.findSubmissionsByAssignment(assignment));
        return "professor_assignment_detail";

    }
}