package com.example.newhisolve.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingController {
    @GetMapping("/setting")
    public String settingPage() {
        return "setting";
    }
}
