package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.TestCase;
import jakarta.transaction.Transactional;

import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(Assignment assignment, Long courseId);
    Assignment findById(Long id);

    List<TestCase> getTestCasesForAssignment(Long assignmentId);

    Assignment getAssignmentById(Long assignmentId);

    @Transactional
    void deleteAssignmentById(Long id);

    @Transactional
    void updateAssignment(Assignment assignment, Long courseId);

    void deleteTestCasesByAssignmentId(Long assignmentId);

    }
