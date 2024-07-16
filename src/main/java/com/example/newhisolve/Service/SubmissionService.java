package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Transactional
    public Submission saveSubmission(Assignment assignment, User student, String code, String language, int totalTestCases, int passedTestCases) {
        Submission existingSubmission = submissionRepository.findByStudentAndAssignment(student, assignment);
        if (existingSubmission != null) {
            submissionRepository.deleteByStudentAndAssignment(student, assignment);
        }

        // 새로운 제출 데이터 저장
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setCode(code);
        submission.setLanguage(language);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setResult(passedTestCases + "/" + totalTestCases);
        submission.setStudent(student);

        return submissionRepository.save(submission);
    }
}
