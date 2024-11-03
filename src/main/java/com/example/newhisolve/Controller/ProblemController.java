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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
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
                                @RequestParam(required = false) List<Boolean> isHidden,
                                @RequestParam("dueDate") String dueDate,
                                Principal principal) {

        if (inputs.size() != outputs.size()) {
            throw new IllegalArgumentException("입력과 출력 리스트의 크기가 일치하지 않습니다.");
        }

        // isHidden이 null일 경우, false로 채움
        if (isHidden == null) {
            isHidden = new ArrayList<>(Collections.nCopies(inputs.size(), false));
        }

        // isHidden 크기와 입력 크기가 다른 경우 isHidden을 입력 크기에 맞춤
        if (isHidden.size() != inputs.size()) {
            for (int i = isHidden.size(); i < inputs.size(); i++) {
                isHidden.add(false); // 부족한 경우 false 추가
            }
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        problem.setDueDate(localDateTime);

        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCase.setHidden(isHidden.get(i));
            testCases.add(testCase);
        }

        problem.setTestCases(testCases);
        problem.setTestcaseCount(testCases.size());  // 테스트 케이스의 개수를 설정

        problemService.createProblem(problem, contestId);

        return "redirect:/admin_contest/" + contestId;
    }


    @PostMapping("/problem/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProblem(@ModelAttribute Problem problem,
                                @RequestParam Long contestId,
                                @RequestParam List<String> inputs,
                                @RequestParam List<String> outputs,
                                @RequestParam("dueDate") String dueDate,
                                Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!user.getRole().equals("ADMIN")) {
            return "redirect:/dashboard";
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dueDate);
        problem.setDueDate(localDateTime);
        problem.setLastModifiedDate(LocalDateTime.now());

        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            TestCase testCase = new TestCase();
            testCase.setInput(inputs.get(i));
            testCase.setExpectedOutput(outputs.get(i));
            testCases.add(testCase);
        }

        problem.setTestcaseCount(inputs.size());
        problem.setTestCases(testCases);

        problemService.updateProblem(problem, contestId);

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

//    \
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
