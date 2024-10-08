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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 역순으로 정렬
        Collections.reverse(contests);

        model.addAttribute("user", user);
        model.addAttribute("contests", contests);

        // 대회 상태 및 참여자 수를 Map에 추가
        Map<Long, Long> participantCounts = new HashMap<>();
        for (Contest contest : contests) {
            long participantCount = contestService.getParticipantCount(contest.getId());
            participantCounts.put(contest.getId(), participantCount);
        }
        model.addAttribute("participantCounts", participantCounts);

        return "dashboard";
    }

}
