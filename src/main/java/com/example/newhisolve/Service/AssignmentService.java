package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(Assignment assignment, Long couseId);
    Assignment findById(Long id);
    void submitAssignment(Long assignmentId, String code, String studentUsername);
    List<Submission> findSubmissionsByAssignment(Assignment assignment);
}

