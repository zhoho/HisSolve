package com.example.newhisolve.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String Welcomepage(){
        return "welcome";
    }

    @GetMapping("/HisSolve")
    public String root(){
        return "welcome";
    }

}
