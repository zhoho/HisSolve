package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Service.ProblemService;
import com.example.newhisolve.Service.ContestService;
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
public class ProblemController {

    private final ProblemService problemService;
    private final UserService userService;
    private final ContestService contestService;
    private final SubmissionService submissionService;

    @GetMapping("/problem/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateProblemForm(@RequestParam Long contestId, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/dashboard";
        }
        Contest contest = contestService.findById(contestId);
        model.addAttribute("problem", new Problem());
        model.addAttribute("contest", contest);
        return "create_problem";
    }

    @PostMapping("/problem/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createProblem(@ModelAttribute Problem problem,
                                @RequestParam Long contestId,
                                @RequestParam List<String> inputs,
                                @RequestParam List<String> outputs,
                                @RequestParam(required = false) List<String> gradingInputs,
                                @RequestParam(required = false) List<String> gradingOutputs,
                                @RequestParam("dueDate") String dueDate,
                                Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/dashboard";
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        problem.setDueDate(localDateTime);

        // Process regular test cases
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);
        }

        problem.setTestcaseCount(String.valueOf(inputs.size()));
        problem.setTestCases(testCases);

        // Process grading test cases
        if (gradingInputs != null && gradingOutputs != null) {
            List<GradingTestCase> gradingTestCases = new ArrayList<>();
            for (int i = 0; i < gradingInputs.size(); i++) {
                GradingTestCase gradingTestCase = new GradingTestCase();
                gradingTestCase.setInput(gradingInputs.get(i));
                gradingTestCase.setExpectedOutput(gradingOutputs.get(i));
                gradingTestCases.add(gradingTestCase);
            }

            problem.setGradingTestcaseCount(String.valueOf(gradingInputs.size()));
            problem.setGradingTestCases(gradingTestCases);
        } else {
            problem.setGradingTestcaseCount(String.valueOf(0));
        }

        problemService.createProblem(problem, contestId);

        return "redirect:/admin_contest/" + contestId;
    }

    @GetMapping("/problem/{id}")
    public String solveProblem(@PathVariable Long id, Principal principal) {
        Problem problem = problemService.findById(id);
        Contest contest = problem.getContest();
        return "redirect:/submit?problemId=" + id + "&language=" + contest.getLanguage();
    }

    @GetMapping("/admin_problem/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewProblem(@PathVariable Long id, Model model, Principal principal) {
        Problem problem = problemService.findById(id);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("problem", problem);
        model.addAttribute("user", user);
        model.addAttribute("submissions", submissionService.findSubmissionsByProblem(problem));
        return "problem_view";
    }

    @PostMapping("/problem/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProblem(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/dashboard";
        }
        Problem problem = problemService.findById(id);
        Long contestId = problem.getContest().getId();
        submissionService.deleteSubmissionsByProblemId(id);
        problemService.deleteProblemById(id);
        return "redirect:/admin_contest/" + contestId;
    }

    @GetMapping("/problem/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditProblemForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/dashboard";
        }
        Problem problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("contest", problem.getContest());
        model.addAttribute("testCases", problem.getTestCases());
        return "edit_problem";
    }

    @PostMapping("/problem/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProblem(@ModelAttribute Problem problem,
                                @RequestParam Long contestId,
                                @RequestParam List<String> inputs,
                                @RequestParam List<String> outputs,
                                @RequestParam(required = false) List<String> gradingInputs,
                                @RequestParam(required = false) List<String> gradingOutputs,
                                @RequestParam("dueDate") String dueDate,
                                Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/dashboard";
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul")).withZoneSameInstant(ZoneId.of("UTC"));
        problem.setDueDate(zonedDateTime.toLocalDateTime());
        problem.setLastModifiedDate(LocalDateTime.now());

        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);
        }

        problem.setTestcaseCount(String.valueOf(inputs.size()));
        problem.setTestCases(testCases);

        if (gradingInputs != null && gradingOutputs != null) {
            List<GradingTestCase> gradingTestCases = new ArrayList<>();
            for (int i = 0; i < gradingInputs.size(); i++) {
                GradingTestCase gradingTestCase = new GradingTestCase();
                gradingTestCase.setInput(gradingInputs.get(i));
                gradingTestCase.setExpectedOutput(gradingOutputs.get(i));
                gradingTestCases.add(gradingTestCase);
            }

            problem.setGradingTestcaseCount(String.valueOf(gradingInputs.size()));
            problem.setGradingTestCases(gradingTestCases);
        } else {
            problem.setGradingTestcaseCount(String.valueOf(0));
        }

        problemService.updateProblem(problem, contestId);

        return "redirect:/admin_contest/" + contestId;
    }

    @GetMapping("/admin_problem_detail/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String detailProblem(@PathVariable Long id, Principal principal, Model model) {
        Problem problem = problemService.findById(id);
        User user = userService.findByUsername(principal.getName());
        Contest contest = problem.getContest();
        model.addAttribute("problem", problem);
        model.addAttribute("user", user);
        model.addAttribute("contest", contest);
        model.addAttribute("submissions", submissionService.findSubmissionsByProblem(problem));
        return "admin_problem_detail";
    }
}
