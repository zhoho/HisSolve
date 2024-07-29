package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "edit_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(User user, Principal principal) {
        String currentUsername = principal.getName();
        User existingUser = userService.findByUsername(currentUsername);

        // Update the email
        existingUser.setEmail(user.getEmail());

        userService.updateUser(existingUser);

        return "redirect:/dashboard";
    }

}
