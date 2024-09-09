package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        System.out.println("Registering user: " + user.getUsername());  // 디버깅용 로그
        userService.register(user);
        return "redirect:/adminLogin";
    }


    @GetMapping("/adminLogin")
    public String showLoginForm() {
        return "admin_login";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
