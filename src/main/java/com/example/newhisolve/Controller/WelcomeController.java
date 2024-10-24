package com.example.newhisolve.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/HisSolve")
    public String Welcomepage() {
        return "welcome";
    }

    @GetMapping("/")
    public String root() {
        return "welcome";
    }
}
