package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;

import java.util.List;
import java.util.Map;

public interface CourseService {
    Course createCourse(Course course, String professorUsername);

    void joinCourse(String code, String studentUsername);

    Course findById(Long id);

    List<Course> findByUser(User user);

    List<Assignment> findAssignmentsByCourse(Course course);

    void updateCourse(Course course);

    List<Course> findCoursesByProfessor(User professor);

    List<User> getSortedStudentsByTotalScore(Long courseId);

    Map<Long, List<Integer>> getStudentAssignmentScores(Long courseId);

    int getScoreByStudentAndAssignment(Long studentId, Long assignmentId);

    void deleteCourse(Course course);

    void removeStudentFromCourse(Long courseId, Long studentId);

    int getTotalScoreByStudentAndCourse(Long studentId, Long courseId);
}
