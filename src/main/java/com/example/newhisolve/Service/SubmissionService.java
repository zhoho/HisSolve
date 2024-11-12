package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.SavedCode;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.dto.SubmissionDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SubmissionService {

    Submission saveSubmission(SubmissionDTO submissionDTO);

    SavedCode saveCode(SubmissionDTO submissionDTO);

    Optional<SavedCode> getSavedCode(Long problemId, Long userId);

    Optional<Submission> getSubmission(Long submissionId);

    List<Submission> findByProblemId(Long problemId);

    void deleteSubmissionsByProblemId(Long problemId);

    void deleteSubmissionsByUserId(Long userId);

    void deleteSavedCodeByUserId(Long userId);

    List<Submission> findByProblemAndUser(Long problemId, Long userId);

    List<Submission> findSubmissionsByProblem(Problem problem);

    // 문제에 대한 모든 테스트케이스를 가져오는 메서드 추가
    List<TestCase> getTestCases(Long problemId);

    // 코드를 실행하고 그 결과를 반환하는 메서드 추가
    String executeCode(String code, String input, String language);
}
