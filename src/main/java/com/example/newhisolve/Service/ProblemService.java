package com.example.newhisolve.Service;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.TestCase;
import java.util.List;

public interface ProblemService {
    Problem createProblem(Problem problem, Long contestId);

    Problem findById(Long id);

    List<TestCase> getTestCasesForProblem(Long problemId);

    Problem getProblemById(Long problemId);

    void deleteProblemById(Long id);

    void updateProblem(Problem problem, Long contestId);

    int getProblemCountById(Long id);
}
