package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;


    @Override
    public Assignment createAssignment(Assignment assignment, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        assignment.setCourse(course);
        assignment.setCreateDate(LocalDateTime.now());
        assignment.setLastModifiedDate(LocalDateTime.now());
        assignment.getDueDate();
        assignmentRepository.save(assignment);
        return assignmentRepository.save(assignment);
    }
    @Override
    public List<TestCase> getTestCasesForAssignment(Long assignmentId) {
        Assignment assignment = findById(assignmentId);
        return assignment.getTestCases();
    }

    @Override
    public List<GradingTestCase> getGradingTestCasesForAssignment(Long assignmentId) {
        Assignment assignment = findById(assignmentId);
        return assignment.getGradingTestCases();
    }

    @Override
    public Assignment findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        if (assignment.isPresent()) {
            Assignment foundAssignment = assignment.get();
            // Log assignment and test cases for debugging
            System.out.println("Found Assignment: " + foundAssignment.getId());
            for (TestCase testCase : foundAssignment.getTestCases()) {
                System.out.println("Test Case - Input: " + testCase.getInput() + ", Expected Output: " + testCase.getExpectedOutput());
            }
            return foundAssignment;
        } else {
            throw new RuntimeException("Assignment not found");
        }
    }


    @Override
    public Assignment getAssignmentById(Long assignmentId) {
        Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
        if (assignment.isPresent()) {
            return assignment.get();
        } else {
            throw new RuntimeException("Assignment를 찾을 수 없습니다: " + assignmentId);
        }
    }

    @Override
    @Transactional
    public void deleteTestCasesByAssignmentId(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new IllegalArgumentException("Invalid assignment Id:" + assignmentId));
        assignment.getTestCases().clear();
        assignmentRepository.save(assignment);
    }


    @Transactional
    @Override
    public void deleteAssignmentById(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateAssignment(Assignment assignment, Long courseId) {
        Assignment existingAssignment = assignmentRepository.findById(assignment.getId()).orElseThrow();
        existingAssignment.setTitle(assignment.getTitle());
        existingAssignment.setDueDate(assignment.getDueDate());
        existingAssignment.setDescription(assignment.getDescription());
        existingAssignment.setTestCases(assignment.getTestCases());
        existingAssignment.setLastModifiedDate(assignment.getLastModifiedDate());
        existingAssignment.setGradingTestcaseCount(assignment.getGradingTestcaseCount());
        existingAssignment.setGradingTestCases(assignment.getGradingTestCases());
        existingAssignment.setLastModifiedDate(assignment.getLastModifiedDate());

        assignmentRepository.save(existingAssignment);
    }
}
