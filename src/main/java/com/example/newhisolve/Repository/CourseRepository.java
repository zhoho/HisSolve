package com.example.newhisolve.Repository;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByProfessor(User professor);
    List<Course> findByStudentsContaining(User student);
}
