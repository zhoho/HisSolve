package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByCourse(Contest course);
    List<Problem> findByCourseId(Long courseId);
}
