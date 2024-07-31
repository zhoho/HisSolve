package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.SavedCode; // 추가
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.SavedCodeRepository; // 추가
import com.example.newhisolve.Repository.UserRepository;
import com.example.newhisolve.dto.SubmissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SavedCodeRepository savedCodeRepository; // 추가

    @Transactional
    public Submission saveSubmission(SubmissionDTO submissionDTO) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();
        Optional<Submission> submissionOptional = submissionRepository.findByAssignmentAndStudent(assignment, student);

        Submission submission;
        if (submissionOptional.isPresent()) {
            submission = submissionOptional.get(); // 기존 제출물 업데이트
        } else {
            submission = new Submission(); // 새로운 제출물 생성
            submission.setAssignment(assignment);
            submission.setStudent(student);
        }

        submission.setCode(submissionDTO.getCode());
        submission.setLanguage(submissionDTO.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setPass_count(String.valueOf(submissionDTO.getPassCount()));

        int passCount = Integer.parseInt(submissionDTO.getPassCount());
        boolean allTestsPassed = passCount == assignment.getTestCases().size();
        submission.setResult(allTestsPassed ? "성공" : "실패");

        return submissionRepository.save(submission);
    }

    @Transactional
    public SavedCode saveCode(SubmissionDTO submissionDTO) { // 변경
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();
        SavedCode savedCode = savedCodeRepository.findByAssignmentAndStudent(assignment, student)
                .orElse(new SavedCode());

        savedCode.setAssignment(assignment);
        savedCode.setStudent(student);
        savedCode.setCode(submissionDTO.getCode());
        savedCode.setLanguage(submissionDTO.getLanguage());
        savedCode.setLastSavedDate(submissionDTO.getLastSavedDate());

        return savedCodeRepository.save(savedCode);
    }

    public Optional<SavedCode> getSavedCode(Long assignmentId, Long studentId) { // 변경
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();

        System.out.println("Assignment ID: " + assignment.getId());
        System.out.println("Student ID: " + student.getId());

        Optional<SavedCode> savedCodeOptional = savedCodeRepository.findByAssignmentAndStudent(assignment, student); // 변경

        if (savedCodeOptional.isPresent()) {
            System.out.println("SavedCode found with code: " + savedCodeOptional.get().getCode());
        } else {
            System.out.println("No SavedCode found for this assignment and student");
        }

        return savedCodeOptional;
    }

    public List<Submission> findByAssignmentId(Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        submissions.forEach(submission -> logger.info(submission.toString()));
        return submissions;
    }

    @Transactional
    public void deleteSubmissionsByAssignmentId(Long assignmentId) {
        submissionRepository.deleteByAssignmentId(assignmentId);
    }

    public List<Submission> findByAssignmentAndStudent(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElse(null);
    }

    public List<Submission> findSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    public void deleteSubmissionById(Long id) {
        submissionRepository.deleteById(id);
    }
}
