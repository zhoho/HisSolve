package com.example.newhisolve.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String showIndexPage(@RequestParam("assignmentId") Long assignmentId,
                                @RequestParam("language") String language,
                                Model model) {
        model.addAttribute("assignmentId", assignmentId);
        model.addAttribute("language", language);
        return "index";
    }
}
