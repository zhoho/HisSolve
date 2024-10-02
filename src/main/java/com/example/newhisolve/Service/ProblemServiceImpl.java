package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    private final ContestRepository contestRepository;

    @Override
    public Problem createProblem(Problem problem, Long contestId) {
        Contest contest = contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("Contest not found"));
        problem.setContest(contest);
        problem.setCreateDate(LocalDateTime.now());
        problem.setLastModifiedDate(LocalDateTime.now());
        problem.getDueDate();
        problemRepository.save(problem);
        return problemRepository.save(problem);
    }

    @Override
    public List<TestCase> getTestCasesForProblem(Long problemId) {
        Problem problem = findById(problemId);
        return problem.getTestCases();
    }

    @Override
    public List<GradingTestCase> getGradingTestCasesForProblem(Long problemId) {
        Problem problem = findById(problemId);
        return problem.getGradingTestCases();
    }

    @Override
    public Problem findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        Optional<Problem> problem = problemRepository.findById(id);
        if (problem.isPresent()) {
            Problem foundProblem = problem.get();
            // Log problem and test cases for debugging
            System.out.println("Found Problem: " + foundProblem.getId());
            for (TestCase testCase : foundProblem.getTestCases()) {
                System.out.println("Test Case - Input: " + testCase.getInput() + ", Expected Output: " + testCase.getExpectedOutput());
            }
            return foundProblem;
        } else {
            throw new RuntimeException("Problem not found");
        }
    }

    @Override
    public Problem getProblemById(Long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            return problem.get();
        } else {
            throw new RuntimeException("Problem을 찾을 수 없습니다: " + problemId);
        }
    }

    @Transactional
    @Override
    public void deleteProblemById(Long id) {
        problemRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateProblem(Problem problem, Long contestId) {
        // 기존 문제를 조회, 존재하지 않으면 예외 발생
        Problem existingProblem = problemRepository.findById(problem.getId())
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problem.getId()));

        // 컨테스트 ID가 주어졌다면 해당 문제의 컨테스트를 업데이트
        if (contestId != null) {
            Contest contest = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found with id: " + contestId));
            existingProblem.setContest(contest);
        }

        // 문제의 필드들을 업데이트
        existingProblem.setTitle(problem.getTitle());
        existingProblem.setDueDate(problem.getDueDate());
        existingProblem.setDescription(problem.getDescription());
        existingProblem.setTestCases(problem.getTestCases());
        existingProblem.setGradingTestcaseCount(problem.getGradingTestcaseCount());
        existingProblem.setGradingTestCases(problem.getGradingTestCases());

        // 마지막 수정 날짜 업데이트
        existingProblem.setLastModifiedDate(LocalDateTime.now());

        // 업데이트된 문제를 저장
        problemRepository.save(existingProblem);
    }

}
