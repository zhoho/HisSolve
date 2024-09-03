package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
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

        // 학생별 문제별 점수 리스트 계산
        Map<Long, List<Integer>> studentAssignmentScores = courseService.getStudentAssignmentScores(id);
        model.addAttribute("studentAssignmentScores", studentAssignmentScores);

        return "rank_dashboard";
    }

    @GetMapping("/course/export")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<byte[]> exportCourseToExcel(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        List<User> students = course.getStudents();

        // 학생별 과제 점수 데이터를 가져옴
        Map<Long, List<Integer>> studentAssignmentScores = courseService.getStudentAssignmentScores(courseId);

        // 과제의 최대 개수를 동적으로 계산
        int maxAssignments = studentAssignmentScores.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Serial No.");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");

            // 과제 세부 정보 헤더 추가 (예: 문제 1, 문제 2, ... 문제 N)
            for (int i = 1; i <= maxAssignments; i++) {
                headerRow.createCell(2 + i).setCellValue("문제 " + i);
            }

            headerRow.createCell(3 + maxAssignments).setCellValue("Total Score");

            // Body
            int rowNum = 1;
            for (User student : students) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum); // 일련번호
                row.createCell(1).setCellValue(student.getUsername());
                row.createCell(2).setCellValue(student.getEmail());

                // 학생의 각 과제 점수 가져오기
                List<Integer> scores = studentAssignmentScores.get(student.getId());
                if (scores != null) {
                    for (int i = 0; i < scores.size(); i++) {
                        row.createCell(3 + i).setCellValue(scores.get(i)); // 점수 저장
                    }
                }

                int totalScore = courseService.getTotalScoreByStudentAndCourse(student.getId(), courseId);
                row.createCell(3 + maxAssignments).setCellValue(totalScore);

                rowNum++;
            }

            // Write the output to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            // 파일 이름에 course.name과 오늘 날짜를 포함
            String fileName = URLEncoder.encode(course.getName() + "_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            fileName = fileName.replaceAll("\\+", "%20"); // 공백을 %20으로 변경

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel file", e);
        }
    }


}
