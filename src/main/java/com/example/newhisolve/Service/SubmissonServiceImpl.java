package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.ProblemRepository;
import com.example.newhisolve.Repository.SavedCodeRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import com.example.newhisolve.dto.SubmissionDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissonServiceImpl implements SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;

    private final ProblemRepository problemRepository;

    private final UserRepository userRepository;

    private final SavedCodeRepository savedCodeRepository;

    @Override
    @Transactional
    public Submission saveSubmission(SubmissionDTO submissionDTO) {
        Optional<Problem> problemOptional = problemRepository.findById(submissionDTO.getProblemId());
        Optional<User> userOptional = userRepository.findById(submissionDTO.getUserId());

        if (!problemOptional.isPresent() || !userOptional.isPresent()) {
            throw new IllegalArgumentException("Problem or User not found");
        }

        Problem problem = problemOptional.get();
        User user = userOptional.get();
        Contest contest = problem.getContest(); // problem로부터 contest 가져오기

        Optional<Submission> submissionOptional = submissionRepository.findByProblemAndUser(problem, user);

        Submission submission;
        if (submissionOptional.isPresent()) {
            submission = submissionOptional.get(); // 기존 제출물 업데이트
        } else {
            submission = new Submission(); // 새로운 제출물 생성
            submission.setProblem(problem);
            submission.setUser(user);
            submission.setContest(contest); // Contest 설정
        }

        submission.setCode(submissionDTO.getCode());
        submission.setLanguage(submissionDTO.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setPass_count(submissionDTO.getPassCount());

        int passCount;
        try {
            passCount = Integer.parseInt(submissionDTO.getPassCount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid pass count format");
        }

        int totalTestCases = problem.getGradingTestCases().size();
        int scorePerTestCase = 100 / totalTestCases;
        int totalScore = passCount * scorePerTestCase;
        submission.setScore(totalScore);

        boolean allTestsPassed = passCount == totalTestCases;
        submission.setResult(allTestsPassed ? "성공" : "실패");

        return submissionRepository.save(submission);
    }

    @Override
    @Transactional
    public SavedCode saveCode(SubmissionDTO submissionDTO) {
        Optional<Problem> problemOptional = problemRepository.findById(submissionDTO.getProblemId());
        Optional<User> userOptional = userRepository.findById(submissionDTO.getUserId());

        if (!problemOptional.isPresent() || !userOptional.isPresent()) {
            throw new IllegalArgumentException("Problem or User not found");
        }

        Problem problem = problemOptional.get();
        User user = userOptional.get();
        SavedCode savedCode = savedCodeRepository.findByProblemAndUser(problem, user)
                .orElse(new SavedCode());

        savedCode.setProblem(problem);
        savedCode.setUser(user);
        savedCode.setCode(submissionDTO.getCode());
        savedCode.setLanguage(submissionDTO.getLanguage());
        savedCode.setLastSavedDate(submissionDTO.getLastSavedDate());

        return savedCodeRepository.save(savedCode);
    }

    @Override
    public Optional<SavedCode> getSavedCode(Long problemId, Long userId) {
        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (!problemOptional.isPresent() || !userOptional.isPresent()) {
            throw new IllegalArgumentException("Problem or User not found");
        }

        Problem problem = problemOptional.get();
        User user = userOptional.get();

        return savedCodeRepository.findByProblemAndUser(problem, user);
    }


    @Override
    public Optional<Submission> getSubmission(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (!submissionOptional.isPresent()) {
            throw new IllegalArgumentException("Submission not found");
        }
        return submissionOptional;
    }

    @Override
    public List<Submission> findByProblemId(Long problemId) {
        List<Submission> submissions = submissionRepository.findByProblemId(problemId);
        submissions.forEach(submission -> logger.info(submission.toString()));
        return submissions;
    }

    @Override
    @Transactional
    public void deleteSubmissionsByProblemId(Long problemId) {
        submissionRepository.deleteByProblemId(problemId);
    }

    @Override
    public List<Submission> findByProblemAndUser(Long problemId, Long userId) {
        return submissionRepository.findByProblemIdAndUserId(problemId, userId);
    }

    @Override
    public List<Submission> findSubmissionsByProblem(Problem problem) {
        return submissionRepository.findByProblem(problem);
    }

    @Override
    public String getGradingTestcaseCount(Long problemId) {
        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        if (problemOptional.isPresent()) {
            return problemOptional.get().getGradingTestcaseCount();
        } else {
            throw new IllegalArgumentException("Problem not found");
        }
    }
}
