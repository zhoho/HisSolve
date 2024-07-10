package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourse(Course course);
}
