package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.GradingTestCase;
import com.example.newhisolve.Model.TestCase;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ProblemService {
    Problem createProblem(Problem problem, Long contestId);

    Problem findById(Long id);

    List<TestCase> getTestCasesForProblem(Long problemId);

    List<GradingTestCase> getGradingTestCasesForProblem(Long problemId);

    Problem getProblemById(Long problemId);

    @Transactional
    void deleteProblemById(Long id);

    @Transactional
    void updateProblem(Problem problem, Long contestId);
}
