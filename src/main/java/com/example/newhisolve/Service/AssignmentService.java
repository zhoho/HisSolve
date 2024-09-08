package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.GradingTestCase;
import com.example.newhisolve.Model.TestCase;
import jakarta.transaction.Transactional;

import java.util.List;

public interface AssignmentService {
    Problem createAssignment(Problem assignment, Long courseId);

    Problem findById(Long id);

    List<TestCase> getTestCasesForAssignment(Long assignmentId);

    List<GradingTestCase> getGradingTestCasesForAssignment(Long assignmentId);

    Problem getAssignmentById(Long assignmentId);

    @Transactional
    void deleteAssignmentById(Long id);

    @Transactional
    void updateAssignment(Problem assignment, Long courseId);
    }
