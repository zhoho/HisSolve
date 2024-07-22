package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/course/create")
    public String showCreateCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "create_course";
    }

    @PostMapping("/course/create")
    public String createCourse(@ModelAttribute Course course, Principal principal) {
        courseService.createCourse(course, principal.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/course/join")
    public String showJoinCourseForm() {
        return "join_course";
    }

    @PostMapping("/course/join")
    public String joinCourse(@RequestParam String code, Principal principal) {
        courseService.joinCourse(code, principal.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/course/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        Course courseEntity = courseService.findById(id);
        model.addAttribute("course", courseEntity);
        model.addAttribute("assignments", courseService.findAssignmentsByCourse(courseEntity));
        return "course_detail";
    }



    @GetMapping("/professor_course/{id}")
    public String viewProfessorCourse(@PathVariable Long id, Model model) {
        Course courseEntity = courseService.findById(id);
        model.addAttribute("course", courseEntity);
        model.addAttribute("assignments", courseService.findAssignmentsByCourse(courseEntity));
        return "professor_course_detail";
    }


}
