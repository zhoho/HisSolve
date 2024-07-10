package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;

import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(Assignment assignment, Long courseId);
    Assignment findById(Long id);
    List<Submission> findSubmissionsByAssignment(Assignment assignment);
    void submitAssignment(Long assignmentId, String code, String username);
}
