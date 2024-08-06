package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/professor_course/rankDashboard/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String viewRankDashboard(@PathVariable Long id, Model model) {
        Course courseEntity = courseService.findById(id);
        model.addAttribute("course", courseEntity);

        List<User> sortedStudents = courseService.getSortedStudentsByTotalScore(id);
        model.addAttribute("students", sortedStudents);

        // 학생별 총 점수 계산
        Map<Long, Integer> studentTotalScores = new HashMap<>();
        for (User student : sortedStudents) {
            int totalScore = courseService.getTotalScoreByStudentAndCourse(student.getId(), courseEntity.getId());
            studentTotalScores.put(student.getId(), totalScore);
        }
        model.addAttribute("studentTotalScores", studentTotalScores);

        return "rank_dashboard";
    }




    @GetMapping("/course/export")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<byte[]> exportCourseToExcel(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        List<User> students = course.getStudents();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Header
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Name");
            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("Email");
            Cell headerCell3 = headerRow.createCell(2);
            headerCell3.setCellValue("Total Score");

            // Body
            int rowNum = 1;
            for (User student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getUsername());
                row.createCell(1).setCellValue(student.getEmail());
                int totalScore = courseService.getTotalScoreByStudentAndCourse(student.getId(), courseId);
                row.createCell(2).setCellValue(totalScore);
            }

            // Write the output to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=students.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel file", e);
        }
    }
}
