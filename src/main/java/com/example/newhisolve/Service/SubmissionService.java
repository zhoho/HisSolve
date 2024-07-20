package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
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

    @Transactional
    public Submission saveSubmission(Assignment assignment, User student, String code, String language, int totalTestCases, int passedTestCases) {
        List<Submission> existingSubmission = submissionRepository.findByStudentAndAssignment(student, assignment);
        if (existingSubmission != null) {
            submissionRepository.deleteByStudentAndAssignment(student, assignment);
        }
        String result = "성공";
        if(totalTestCases != passedTestCases) {
            result = "실패";
        }
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setCode(code);
        submission.setLanguage(language);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setTotal_count(String.valueOf(totalTestCases));
        submission.setPass_count(String.valueOf(passedTestCases));
        submission.setResult(result);
        submission.setStudent(student);

        return submissionRepository.save(submission);
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

    public Submission saveCode(Long assignmentId, Long studentId, String code, String language) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();

        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
                .orElse(new Submission());
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setCode(code);
        submission.setLanguage(language);
        submission.setLastSavedDate(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public Optional<Submission> getSavedCode(Long assignmentId, Long studentId) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        return submissionRepository.findByAssignmentAndStudent(assignmentOptional.get(), studentOptional.get());
    }
}
