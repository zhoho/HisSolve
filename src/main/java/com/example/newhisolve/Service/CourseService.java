package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;

import java.util.List;
import java.util.Map;

public interface CourseService {
    Contest createCourse(Contest course, String professorUsername);

    void joinCourse(String code, String studentUsername);

    Contest findById(Long id);

    List<Contest> findByUser(User user);

    List<Problem> findAssignmentsByCourse(Contest course);

    void updateCourse(Contest course);

    List<Contest> findCoursesByProfessor(User professor);

    List<User> getSortedStudentsByTotalScore(Long courseId);

    Map<Long, List<Integer>> getStudentAssignmentScores(Long courseId);

    int getScoreByStudentAndAssignment(Long studentId, Long assignmentId);

    void deleteCourse(Contest course);

    void removeStudentFromCourse(Long courseId, Long studentId);

    int getTotalScoreByStudentAndCourse(Long studentId, Long courseId);
}
