package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CourseController {


    private final CourseService courseService;

    @GetMapping("/course/create")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String showCreateCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "create_course";
    }

    @PostMapping("/course/create")
    @PreAuthorize("hasRole('PROFESSOR')")
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
    @PreAuthorize("hasRole('PROFESSOR')")
    public String viewProfessorCourse(@PathVariable Long id, Model model) {
        Course courseEntity = courseService.findById(id);
        model.addAttribute("course", courseEntity);
        model.addAttribute("students", courseEntity.getStudents());
        model.addAttribute("assignments", courseService.findAssignmentsByCourse(courseEntity));

        // 학생별 총 점수 계산
        Map<Long, Integer> studentTotalScores = new HashMap<>();
        for (User student : courseEntity.getStudents()) {
            int totalScore = courseService.getTotalScoreByStudentAndCourse(student.getId(), courseEntity.getId());
            studentTotalScores.put(student.getId(), totalScore);
        }
        model.addAttribute("studentTotalScores", studentTotalScores);

        return "professor_course_detail";
    }


    @PostMapping("/course/removeStudent")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String removeStudent(@RequestParam Long courseId, @RequestParam Long studentId) {
        courseService.removeStudentFromCourse(courseId, studentId);
        return "redirect:/professor_course/" + courseId;
    }

    @GetMapping("/course/edit")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String editCourse(@RequestParam Long courseId, Model model) {
        Course courseEntity = courseService.findById(courseId);
        model.addAttribute("course", courseEntity);
        return "edit_course";
    }

    @PostMapping("/course/edit")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String updateCourse(@ModelAttribute Course course) {
        courseService.updateCourse(course);
        return "redirect:/professor_course/" + course.getId();
    }

    @PostMapping("/course/delete")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String deleteCourse(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        courseService.deleteCourse(course);
        return "redirect:/dashboard";
    }
}
