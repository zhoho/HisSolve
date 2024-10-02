package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ContestRepository contestRepository;

    // 문제 생성
    @Override
    public Problem createProblem(Problem problem, Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        problem.setContest(contest);
        problem.setCreateDate(LocalDateTime.now());
        problem.setLastModifiedDate(LocalDateTime.now());
        problemRepository.save(problem); // 문제 저장
        return problem;
    }

    // 문제에 대한 모든 테스트케이스 가져오기
    @Override
    public List<TestCase> getTestCasesForProblem(Long problemId) {
        Problem problem = findById(problemId);
        return problem.getTestCases();
    }

    // 특정 문제 찾기
    @Override
    public Problem findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        Optional<Problem> problem = problemRepository.findById(id);
        if (problem.isPresent()) {
            Problem foundProblem = problem.get();
            // 로그를 통해 문제 및 테스트케이스 출력
            System.out.println("Found Problem: " + foundProblem.getId());
            for (TestCase testCase : foundProblem.getTestCases()) {
                System.out.println("Test Case - Input: " + testCase.getInput() + ", Expected Output: " + testCase.getExpectedOutput());
            }
            return foundProblem;
        } else {
            throw new RuntimeException("Problem not found");
        }
    }

    // 문제 ID로 문제 가져오기
    @Override
    public Problem getProblemById(Long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            return problem.get();
        } else {
            throw new RuntimeException("Problem을 찾을 수 없습니다: " + problemId);
        }
    }

    // 문제 삭제
    @Transactional
    @Override
    public void deleteProblemById(Long id) {
        problemRepository.deleteById(id);
    }

    // 문제 업데이트
    @Override
    @Transactional
    public void updateProblem(Problem problem, Long contestId) {
        Problem existingProblem = problemRepository.findById(problem.getId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        // 문제 정보 업데이트
        existingProblem.setTitle(problem.getTitle());
        existingProblem.setDueDate(problem.getDueDate());
        existingProblem.setDescription(problem.getDescription());
        existingProblem.setTestCases(problem.getTestCases()); // 히든 테스트케이스도 함께 관리
        existingProblem.setLastModifiedDate(LocalDateTime.now());

        problemRepository.save(existingProblem); // 변경사항 저장
    }

    @Override
    public int getProblemCountById(Long id) {
        return problemRepository.findTestcaseCountById(id);
    }
}
