package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Submission findByStudentAndAssignment(User student, Assignment assignment);
    void deleteByStudentAndAssignment(User student, Assignment assignment);
}
