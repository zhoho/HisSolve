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

    @Override
    @Transactional
    public void updateProblem(Problem problem, Long contestId) {
        Problem existingProblem = problemRepository.findById(problem.getId()).orElseThrow();
        existingProblem.setTitle(problem.getTitle());
        existingProblem.setDueDate(problem.getDueDate());
        existingProblem.setDescription(problem.getDescription());
        existingProblem.setTestCases(problem.getTestCases());
        existingProblem.setLastModifiedDate(problem.getLastModifiedDate());
        existingProblem.setGradingTestcaseCount(problem.getGradingTestcaseCount());
        existingProblem.setGradingTestCases(problem.getGradingTestCases());
        existingProblem.setLastModifiedDate(problem.getLastModifiedDate());

        problemRepository.save(existingProblem);
    }
}
