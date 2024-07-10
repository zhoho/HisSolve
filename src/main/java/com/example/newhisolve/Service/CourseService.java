package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course, String professorUsername);
    void joinCourse(String code, String studentUsername);
    Course findById(Long id);
    List<Course> findByUser(User user);
    List<Assignment> findAssignmentsByCourse(Course course);
}
