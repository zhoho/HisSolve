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

    // 제출 저장
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
        Contest contest = problem.getContest(); // 문제로부터 contest 가져오기

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

        // String 타입의 passCount를 int로 변환
        int passCount;
        try {
            passCount = Integer.parseInt(submissionDTO.getPassCount()); // 문자열을 정수로 변환
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid pass count format");
        }

        // 테스트케이스 리스트에서 히든 포함한 전체 테스트케이스 개수 계산
        List<TestCase> allTestCases = problem.getTestCases();
        int totalTestCases = allTestCases.size();

        // 각 테스트케이스당 점수 계산
        int scorePerTestCase = 100 / totalTestCases;
        int totalScore = passCount * scorePerTestCase;
        submission.setPass_count(String.valueOf(passCount));
        submission.setScore(totalScore);

        // 모든 테스트케이스를 통과했는지 확인
        boolean allTestsPassed = passCount == totalTestCases;
        submission.setResult(allTestsPassed ? "성공" : "실패");

        return submissionRepository.save(submission);
    }

    // 코드 저장
    @Override
    @Transactional
    public SavedCode saveCode(SubmissionDTO submissionDTO) {
        Problem problem = problemRepository.findById(submissionDTO.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        User user = userRepository.findById(submissionDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        SavedCode savedCode = savedCodeRepository.findByProblemAndUser(problem, user)
                .orElse(new SavedCode());

        savedCode.setProblem(problem);
        savedCode.setUser(user);
        savedCode.setCode(submissionDTO.getCode());
        savedCode.setLanguage(submissionDTO.getLanguage());
        savedCode.setLastSavedDate(LocalDateTime.now());

        return savedCodeRepository.save(savedCode);
    }

    // 저장된 코드 가져오기
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

    // 제출물 ID로 제출물 찾기
    @Override
    public Optional<Submission> getSubmission(Long submissionId) {
        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (!submissionOptional.isPresent()) {
            throw new IllegalArgumentException("Submission not found");
        }
        return submissionOptional;
    }

    // 특정 문제 ID로 제출물 리스트 가져오기
    @Override
    public List<Submission> findByProblemId(Long problemId) {
        List<Submission> submissions = submissionRepository.findByProblemId(problemId);
        submissions.forEach(submission -> logger.info(submission.toString()));
        return submissions;
    }

    // 문제 ID로 제출물 삭제
    @Override
    @Transactional
    public void deleteSubmissionsByProblemId(Long problemId) {
        submissionRepository.deleteByProblemId(problemId);
    }

    @Override
    public void deleteSubmissionsByUserId(Long userId) {
        submissionRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteSavedCodeByUserId(Long userId) {
        savedCodeRepository.deleteByUserId(userId);
    }

    // 특정 문제와 유저의 제출물 찾기
    @Override
    public List<Submission> findByProblemAndUser(Long problemId, Long userId) {
        return submissionRepository.findByProblemIdAndUserId(problemId, userId);
    }

    // 특정 문제의 제출물 리스트 가져오기
    @Override
    public List<Submission> findSubmissionsByProblem(Problem problem) {
        return submissionRepository.findByProblem(problem);
    }


    // 문제에 대한 모든 테스트케이스 가져오기
    @Override
    public List<TestCase> getTestCases(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
        return problem.getTestCases();  // 모든 테스트케이스(히든 포함) 반환
    }

    // 코드 실행 및 결과 반환
    @Override
    public String executeCode(String code, String input, String language) {
        // 코드 실행 로직을 구현하고 결과 반환 (예시로써 사용)
        // 실제 환경에 맞게 사용자의 코드를 실행하고, 결과를 받아야 함
        return "";  // 이 부분은 실제 코드 실행 결과로 대체
    }
}
