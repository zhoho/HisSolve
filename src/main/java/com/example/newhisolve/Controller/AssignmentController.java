package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.*;
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
        Contest course = courseService.findById(courseId);
        model.addAttribute("assignment", new Problem());
        model.addAttribute("course", course);
        return "create_assignment";
    }

    @PostMapping("/assignment/create")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String createAssignment(@ModelAttribute Problem assignment,
                                   @RequestParam Long courseId,
                                   @RequestParam List<String> inputs,
                                   @RequestParam List<String> outputs,
                                   @RequestParam(required = false) List<String> gradingInputs,
                                   @RequestParam(required = false) List<String> gradingOutputs,
                                   @RequestParam("dueDate") String dueDate,
                                   Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("PROFESSOR")) {
            return "redirect:/dashboard";
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        assignment.setDueDate(localDateTime);

        // Process regular test cases
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);
        }

        assignment.setTestcaseCount(String.valueOf(inputs.size()));
        assignment.setTestCases(testCases);

        // Process grading test cases
        if (gradingInputs != null && gradingOutputs != null) {
            List<GradingTestCase> gradingTestCases = new ArrayList<>();
            for (int i = 0; i < gradingInputs.size(); i++) {
                GradingTestCase gradingTestCase = new GradingTestCase();
                gradingTestCase.setInput(gradingInputs.get(i));
                gradingTestCase.setExpectedOutput(gradingOutputs.get(i));
                gradingTestCases.add(gradingTestCase);
            }

            assignment.setGradingTestcaseCount(String.valueOf(gradingInputs.size()));
            assignment.setGradingTestCases(gradingTestCases);
        } else {
            assignment.setGradingTestcaseCount(String.valueOf(0));
        }

        assignmentService.createAssignment(assignment, courseId);

        return "redirect:/professor_course/" + courseId;
    }




    @GetMapping("/assignment/{id}")
    public String SolveAssignment(@PathVariable Long id, Principal principal) {
        Problem assignment = assignmentService.findById(id);
        Contest contest = assignment.getContest();
        return "redirect:/index?assignmentId=" + id + "&language=" + contest.getLanguage();
    }

    @GetMapping("/professor_assignment/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String viewAssignment(@PathVariable Long id, Model model, Principal principal) {
        Problem assignment = assignmentService.findById(id);
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
        Problem assignment = assignmentService.findById(id);
        Long courseId = assignment.getContest().getId();
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
        Problem assignment = assignmentService.findById(id);
        model.addAttribute("assignment", assignment);
        model.addAttribute("course", assignment.getContest());
        model.addAttribute("testCases", assignment.getTestCases());
        return "edit_assignment";
    }


    @PostMapping("/assignment/update")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String updateAssignment(@ModelAttribute Problem assignment,
                                   @RequestParam Long courseId,
                                   @RequestParam List<String> inputs,
                                   @RequestParam List<String> outputs,
                                   @RequestParam(required = false) List<String> gradingInputs,
                                   @RequestParam(required = false) List<String> gradingOutputs,
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

        if (gradingInputs != null && gradingOutputs != null) {
            List<GradingTestCase> gradingTestCases = new ArrayList<>();
            for (int i = 0; i < gradingInputs.size(); i++) {
                GradingTestCase gradingTestCase = new GradingTestCase();
                gradingTestCase.setInput(gradingInputs.get(i));
                gradingTestCase.setExpectedOutput(gradingOutputs.get(i));
                gradingTestCases.add(gradingTestCase);
            }

            assignment.setGradingTestcaseCount(String.valueOf(gradingInputs.size()));
            assignment.setGradingTestCases(gradingTestCases);
        } else {
            assignment.setGradingTestcaseCount(String.valueOf(0));
        }

        assignmentService.updateAssignment(assignment, courseId);

        return "redirect:/professor_course/" + courseId;
    }


    @GetMapping("/professor_assignment_detail/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String detailAsignment(@PathVariable Long id, Principal principal, Model model) {
        Problem assignment = assignmentService.findById(id);
        User user = userService.findByUsername(principal.getName());
        Contest course = assignment.getContest();
        model.addAttribute("assignment", assignment);
        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("submissions", submissionService.findSubmissionsByAssignment(assignment));
        return "professor_assignment_detail";

    }
}