package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.TestCase;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ProblemService {
    // 문제 생성
    Problem createProblem(Problem problem, Long contestId);

    // 문제 ID로 문제 찾기
    Problem findById(Long id);

    // 문제에 대한 모든 테스트케이스 가져오기 (히든 포함)
    List<TestCase> getTestCasesForProblem(Long problemId);

    // 문제 ID로 문제 찾기
    Problem getProblemById(Long problemId);

    // 문제 삭제
    @Transactional
    void deleteProblemById(Long id);

    // 문제 업데이트
    @Transactional
    void updateProblem(Problem problem, Long contestId);
}
