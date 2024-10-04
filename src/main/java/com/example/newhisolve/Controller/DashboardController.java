package com.example.newhisolve.Controller;
import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.ContestService;
import com.example.newhisolve.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ContestService contestService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        List<Contest> contests = contestService.findByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("contests", contests);

        // 대회 상태 추가
        contests.forEach(contest -> {
            String status = contest.getStatus();
            model.addAttribute("contestStatus_" + contest.getId(), status); // 각 대회 상태 추가
        });

        return "dashboard";
    }
}
