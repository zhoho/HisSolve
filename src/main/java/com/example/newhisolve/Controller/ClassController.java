package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ClassController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/class/create")
    public String showCreateClassForm(Model model) {
        model.addAttribute("class");
        return "create_class";
    }

    @PostMapping("/class/create")
    public String createClass(@ModelAttribute Course courseEntity, Principal principal) {
        courseService.createCourse(courseEntity, principal.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/class/join")
    public String showJoinClassForm() {
        return "join_class";
    }

    @PostMapping("/class/join")
    public String joinClass(@RequestParam String code, Principal principal) {
        courseService.joinCourse(code, principal.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/class/{id}")
    public String viewClass(@PathVariable Long id, Model model) {
        Course courseEntity = courseService.findById(id);
        model.addAttribute("class", courseEntity);
        model.addAttribute("assignments", courseService.findAssignmentsByCourse(courseEntity));
        return "class_detail";
    }
}
