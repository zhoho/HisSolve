package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment(Assignment assignment);
}
