package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.SavedCode;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.SavedCodeRepository;
import com.example.newhisolve.Repository.UserRepository;
import com.example.newhisolve.dto.SubmissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private SavedCodeRepository savedCodeRepository;

    @Transactional
    public Submission saveSubmission(SubmissionDTO submissionDTO) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();
        Course course = assignment.getCourse(); // assignment로부터 course 가져오기

        Optional<Submission> submissionOptional = submissionRepository.findByAssignmentAndStudent(assignment, student);

        Submission submission;
        if (submissionOptional.isPresent()) {
            submission = submissionOptional.get(); // 기존 제출물 업데이트
        } else {
            submission = new Submission(); // 새로운 제출물 생성
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setCourse(course); // Course 설정
        }

        submission.setCode(submissionDTO.getCode());
        submission.setLanguage(submissionDTO.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setPass_count(submissionDTO.getPassCount());

        int passCount;
        try {
            passCount = Integer.parseInt(submissionDTO.getPassCount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid pass count format");
        }

        int totalTestCases = assignment.getTestCases().size();
        int scorePerTestCase = 100 / totalTestCases;
        int totalScore = passCount * scorePerTestCase;
        submission.setScore(totalScore);

        boolean allTestsPassed = passCount == totalTestCases;
        submission.setResult(allTestsPassed ? "성공" : "실패");

        return submissionRepository.save(submission);
    }

    @Transactional
    public SavedCode saveCode(SubmissionDTO submissionDTO) {
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

    public Optional<SavedCode> getSavedCode(Long assignmentId, Long studentId) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();

        Optional<SavedCode> savedCodeOptional = savedCodeRepository.findByAssignmentAndStudent(assignment, student);

        return savedCodeOptional;
    }

    public Optional<Submission> getSubmission(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (!submissionOptional.isPresent()) {
            throw new IllegalArgumentException("Submission not found");
        }
        return submissionOptional;
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

    public List<Submission> findSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }
}
