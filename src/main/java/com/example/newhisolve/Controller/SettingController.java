package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.ContestService;
import com.example.newhisolve.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private final UserService userService;
    private final ContestService contestService;

    @GetMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public String showSettingsPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Contest> contests = contestService.findContestsByAdmin(user);
        model.addAttribute("user", user);
        model.addAttribute("contests", contests);
        return "settings";
    }

    @PostMapping("/updateProfile")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProfile(@ModelAttribute User user, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setDepartment(user.getDepartment());
        userService.updateUser(currentUser);
        return "redirect:/settings";
    }

    @PostMapping("/updateContestName")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateContestName(@RequestParam Long contestId, @RequestParam String newName) {
        Contest contest = contestService.findById(contestId);
        contest.setName(newName);
        contestService.updateContest(contest);
        return "redirect:/settings";
    }

    @PostMapping("/deleteContest")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteContest(@RequestParam Long contestId) {
        Contest contest = contestService.findById(contestId);
        contestService.deleteContest(contest);
        return "redirect:/settings";
    }
}
