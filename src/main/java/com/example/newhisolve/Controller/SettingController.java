package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.CourseService;
import com.example.newhisolve.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private final UserService userService;
    private final CourseService courseService;

    @GetMapping("/settings")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String showSettingsPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Course> courses = courseService.findCoursesByProfessor(user);
        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        return "settings";
    }

    @PostMapping("/updateProfile")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String updateProfile(@ModelAttribute User user, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setDepartment(user.getDepartment());
        userService.updateUser(currentUser);
        return "redirect:/settings";
    }

    @PostMapping("/updateCourseName")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String updateCourseName(@RequestParam Long courseId, @RequestParam String newName) {
        Course course = courseService.findById(courseId);
        course.setName(newName);
        courseService.updateCourse(course);
        return "redirect:/settings";
    }

    @PostMapping("deleteCourseName")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String deleteCourseName(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        courseService.deleteCourse(course);
        return "redirect:/settings";
    }
}
