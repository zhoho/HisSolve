package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.SavedCodeRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import com.example.newhisolve.dto.SubmissionDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissonServiceImpl implements SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;

    private final AssignmentRepository assignmentRepository;

    private final UserRepository userRepository;

    private final SavedCodeRepository savedCodeRepository;

    @Override
    @Transactional
    public Submission saveSubmission(SubmissionDTO submissionDTO) {
        Optional<Problem> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Problem assignment = assignmentOptional.get();
        User student = studentOptional.get();
        Contest course = assignment.getContest(); // assignment로부터 course 가져오기

        Optional<Submission> submissionOptional = submissionRepository.findByAssignmentAndStudent(assignment, student);

        Submission submission;
        if (submissionOptional.isPresent()) {
            submission = submissionOptional.get(); // 기존 제출물 업데이트
        } else {
            submission = new Submission(); // 새로운 제출물 생성
            submission.setProblem(assignment);
            submission.setStudent(student);
            submission.setContest(course); // Course 설정
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

        int totalTestCases = assignment.getGradingTestCases().size();
        int scorePerTestCase = 100 / totalTestCases;
        int totalScore = passCount * scorePerTestCase;
        submission.setScore(totalScore);

        boolean allTestsPassed = passCount == totalTestCases;
        submission.setResult(allTestsPassed ? "성공" : "실패");

        return submissionRepository.save(submission);
    }

    @Override
    @Transactional
    public SavedCode saveCode(SubmissionDTO submissionDTO) {
        Optional<Problem> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Problem assignment = assignmentOptional.get();
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

    @Override
    public Optional<SavedCode> getSavedCode(Long assignmentId, Long studentId) {
        Optional<Problem> assignmentOptional = assignmentRepository.findById(assignmentId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Problem assignment = assignmentOptional.get();
        User student = studentOptional.get();

        return savedCodeRepository.findByAssignmentAndStudent(assignment, student);
    }


    @Override
    public Optional<Submission> getSubmission(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (!submissionOptional.isPresent()) {
            throw new IllegalArgumentException("Submission not found");
        }
        return submissionOptional;
    }

    @Override
    public List<Submission> findByAssignmentId(Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        submissions.forEach(submission -> logger.info(submission.toString()));
        return submissions;
    }

    @Override
    @Transactional
    public void deleteSubmissionsByAssignmentId(Long assignmentId) {
        submissionRepository.deleteByAssignmentId(assignmentId);
    }

    @Override
    public List<Submission> findByAssignmentAndStudent(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    @Override
    public List<Submission> findSubmissionsByAssignment(Problem assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    @Override
    public String getGradingTestcaseCount(Long assignmentId) {
        Optional<Problem> assignmentOptional = assignmentRepository.findById(assignmentId);
        if (assignmentOptional.isPresent()) {
            return assignmentOptional.get().getGradingTestcaseCount();
        } else {
            throw new IllegalArgumentException("Assignment not found");
        }
    }
}
