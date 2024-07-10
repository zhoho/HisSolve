package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String viewDashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Course> courses = courseService.findByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        return "dashboard"; // Thymeleaf 템플릿 이름
    }
}
