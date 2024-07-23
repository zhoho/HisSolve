package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
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

    @Transactional
    public Submission saveSubmission(SubmissionDTO submissionDTO) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();
        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
                .orElse(new Submission());

        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setCode(submissionDTO.getCode());
        submission.setLanguage(submissionDTO.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setTotal_count(String.valueOf(submissionDTO.getTotalCount()));
        submission.setPass_count(String.valueOf(submissionDTO.getPassCount()));
        submission.setResult(Objects.equals(submissionDTO.getTotalCount(), submissionDTO.getPassCount()) ? "성공" : "실패");

        return submissionRepository.save(submission);
    }

    public Submission saveCode(SubmissionDTO submissionDTO) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(submissionDTO.getAssignmentId());
        Optional<User> studentOptional = userRepository.findById(submissionDTO.getStudentId());

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();

        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
                .orElse(new Submission());
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setCode(submissionDTO.getCode());
        submission.setLanguage(submissionDTO.getLanguage());
        submission.setLastSavedDate(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public Optional<Submission> getSavedCode(Long assignmentId, Long studentId) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        Optional<User> studentOptional = userRepository.findById(studentId);

        if (!assignmentOptional.isPresent() || !studentOptional.isPresent()) {
            throw new IllegalArgumentException("Assignment or Student not found");
        }

        Assignment assignment = assignmentOptional.get();
        User student = studentOptional.get();

        System.out.println("Assignment ID: " + assignment.getId());
        System.out.println("Student ID: " + student.getId());

        Optional<Submission> submissionOptional = submissionRepository.findByAssignmentAndStudent(assignment, student);

        if (submissionOptional.isPresent()) {
            System.out.println("Submission found with code: " + submissionOptional.get().getCode());
        } else {
            System.out.println("No Submission found for this assignment and student");
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

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElse(null);
    }
}
