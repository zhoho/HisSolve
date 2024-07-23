package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student);
    List<Submission> findByAssignmentId(Long assignmentId);
    void deleteByAssignmentId(Long assignmentId);
    List<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    void deleteByStudentAndAssignment(User student, Assignment assignment);
    List<Submission> findByStudentAndAssignment(User student, Assignment assignment);
}
