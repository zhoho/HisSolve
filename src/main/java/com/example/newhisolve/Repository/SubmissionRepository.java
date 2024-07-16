package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentAndAssignment(User student, Assignment assignment);
    void deleteByStudentAndAssignment(User student, Assignment assignment);
    List<Submission> findByAssignmentId(Long AssignmentId);
}
