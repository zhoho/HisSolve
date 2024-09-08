package com.example.newhisolve.Service;
import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.CourseRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final AssignmentRepository assignmentRepository;

    private final SubmissionRepository submissionRepository;

    @Override
    public Contest createCourse(Contest course, String professorUsername) {
        User professor = userRepository.findByUsername(professorUsername).orElseThrow(() -> new RuntimeException("Professor not found"));
        course.setProfessor(professor);
        course.setCode(UUID.randomUUID().toString().substring(0, 5));
        return courseRepository.save(course);
    }

    @Override
    public void joinCourse(String code, String studentUsername) {
        Contest course = courseRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Course not found"));
        User student = userRepository.findByUsername(studentUsername).orElseThrow(() -> new RuntimeException("Student not found"));
        course.getStudents().add(student);
        courseRepository.save(course);
    }

    @Override
    public Contest findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public List<Contest> findByUser(User user) {
        if (user.getRole().equals("PROFESSOR")) {
            return courseRepository.findByProfessor(user);
        } else {
            return courseRepository.findByStudentsContaining(user);
        }
    }

    @Override
    public List<Problem> findAssignmentsByCourse(Contest course) {
        return assignmentRepository.findByCourse(course);
    }

    @Override
    public void updateCourse(Contest course) {
        // 기존 강의를 데이터베이스에서 불러와서 수정사항을 반영
        Contest existingCourse = courseRepository.findById(course.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        existingCourse.setName(course.getName());
        existingCourse.setDescription(course.getDescription());
        courseRepository.save(existingCourse);
    }

    @Override
    public List<Contest> findCoursesByProfessor(User professor) {
        return courseRepository.findByProfessor(professor);
    }

    @Override
    public List<User> getSortedStudentsByTotalScore(Long courseId) {
        Contest course = findById(courseId);
        List<User> students = course.getStudents();

        return students.stream()
                .sorted(Comparator.comparingInt(student -> getTotalScoreByStudentAndCourse(((User)student).getId(), courseId)).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<Integer>> getStudentAssignmentScores(Long courseId) {
        List<User> students = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"))
                .getStudents();
        List<Problem> assignments = assignmentRepository.findByCourseId(courseId);

        return students.stream().collect(Collectors.toMap(
                User::getId,
                student -> assignments.stream()
                        .map(assignment -> getScoreByStudentAndAssignment(student.getId(), assignment.getId()))
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public int getScoreByStudentAndAssignment(Long studentId, Long assignmentId) {
        return submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId)
                .map(submission -> Optional.ofNullable(submission.getScore()).orElse(0))
                .orElse(0);
    }


    @Override
    public void deleteCourse(Contest course) {
        courseRepository.delete(course);
    }

    @Override
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        Optional<Contest> courseOptional = courseRepository.findById(courseId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (courseOptional.isPresent() && studentOptional.isPresent()) {
            Contest course = courseOptional.get();
            User student = studentOptional.get();

            course.getStudents().remove(student);
            courseRepository.save(course);
        } else {
            throw new IllegalArgumentException("Course or Student not found");
        }
    }

    @Override
    public int getTotalScoreByStudentAndCourse(Long studentId, Long courseId) {
        Integer totalScore = submissionRepository.findTotalScoreByStudentAndCourse(studentId, courseId);
        return totalScore != null ? totalScore : 0;
    }
}
