package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ContestRepository contestRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<TestCase> getTestCasesForProblem(Long problemId) {
        Problem problem = findById(problemId);
        return problem.getTestCases();
    }

    // 특정 문제 찾기
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Problem getProblemById(Long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            return problem.get();
        } else {
            throw new RuntimeException("Problem을 찾을 수 없습니다: " + problemId);
        }
    }

    // 문제 삭제
    @Override
    public void deleteProblemById(Long id) {
        problemRepository.deleteById(id);
    }

    // 문제 업데이트
    @Override
    public void updateProblem(Problem problem, Long contestId) {
        Problem existingProblem = problemRepository.findById(problem.getId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        existingProblem.setTitle(problem.getTitle());
        existingProblem.setDueDate(problem.getDueDate());
        existingProblem.setDescription(problem.getDescription());

        List<TestCase> newTestCases = problem.getTestCases();

        for (TestCase testCase : newTestCases) {
            testCase.setProblem(existingProblem);
        }

        existingProblem.getTestCases().clear();
        existingProblem.getTestCases().addAll(newTestCases);
        existingProblem.setTestcaseCount(problem.getTestcaseCount());
        existingProblem.setLastModifiedDate(LocalDateTime.now());

        problemRepository.save(existingProblem);
    }

    @Override
    @Transactional(readOnly = true)
    public int getProblemCountById(Long id) {
        return problemRepository.findTestcaseCountById(id);
    }
}
