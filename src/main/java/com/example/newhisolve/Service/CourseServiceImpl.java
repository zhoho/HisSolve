package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.CourseRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository; // 추가된 부분

    @Override
    public Course createCourse(Course course, String professorUsername) {
        User professor = userRepository.findByUsername(professorUsername).orElseThrow(() -> new RuntimeException("Professor not found"));
        course.setProfessor(professor);
        course.setCode(UUID.randomUUID().toString().substring(0, 5));
        return courseRepository.save(course);
    }

    @Override
    public void joinCourse(String code, String studentUsername) {
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Course not found"));
        User student = userRepository.findByUsername(studentUsername).orElseThrow(() -> new RuntimeException("Student not found"));
        course.getStudents().add(student);
        courseRepository.save(course);
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public List<Course> findByUser(User user) {
        if (user.getRole().equals("PROFESSOR")) {
            return courseRepository.findByProfessor(user);
        } else {
            return courseRepository.findByStudentsContaining(user);
        }
    }

    @Override
    public List<Assignment> findAssignmentsByCourse(Course course) {
        return assignmentRepository.findByCourse(course);
    }

    @Override
    public void updateCourse(Course course) {
        // 기존 강의를 데이터베이스에서 불러와서 수정사항을 반영
        Course existingCourse = courseRepository.findById(course.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        existingCourse.setName(course.getName());
        existingCourse.setDescription(course.getDescription());
        courseRepository.save(existingCourse);
    }

    @Override
    public List<Course> findCoursesByProfessor(User professor) {
        return courseRepository.findByProfessor(professor);
    }

    @Override
    public List<User> getSortedStudentsByTotalScore(Long courseId) {
        Course course = findById(courseId);
        List<User> students = course.getStudents();

        return students.stream()
                .sorted(Comparator.comparingInt(student -> getTotalScoreByStudentAndCourse(((User)student).getId(), courseId)).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCourse(Course course) {
        courseRepository.delete(course);
    }

    @Override
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (courseOptional.isPresent() && studentOptional.isPresent()) {
            Course course = courseOptional.get();
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
