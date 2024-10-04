package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.ProblemRepository;
import com.example.newhisolve.Repository.ContestRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;

    @Override
    public Contest createContest(Contest contest, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername).orElseThrow(() -> new RuntimeException("Admin not found"));
        contest.setAdmin(admin);
        contest.setCode(UUID.randomUUID().toString().substring(0, 5));
        return contestRepository.save(contest);
    }

    @Override
    public void joinContest(String code, String userUsername) {
        Contest contest = contestRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Contest not found"));
        User user = userRepository.findByUsername(userUsername).orElseThrow(() -> new RuntimeException("User not found"));
        contest.getUsers().add(user);
        contestRepository.save(contest);
    }

    @Override
    public Contest findById(Long id) {
        return contestRepository.findById(id).orElseThrow(() -> new RuntimeException("Contest not found"));
    }

    @Override
    public List<Contest> findByUser(User user) {
        if (user.getRole().equals("ADMIN")) {
            return contestRepository.findByAdmin(user);
        } else {
            return contestRepository.findByUsersContaining(user);
        }
    }

    @Override
    public List<Problem> findProblemsByContest(Contest contest) {
        return problemRepository.findByContest(contest);
    }

    @Override
    public void updateContest(Contest contest) {
        Contest existingContest = contestRepository.findById(contest.getId())
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        existingContest.setName(contest.getName());
        existingContest.setDescription(contest.getDescription());
        contestRepository.save(existingContest);
    }

    @Override
    public List<Contest> findContestsByAdmin(User admin) {
        return contestRepository.findByAdmin(admin);
    }

    @Override
    public List<User> getSortedUsersByTotalScore(Long contestId) {
        Contest contest = findById(contestId); // contestId로 Contest 객체 가져옴
        List<User> users = contest.getUsers(); // Contest에서 참가한 User 목록 가져옴

        return users.stream()
                .sorted((user1, user2) -> {
                    // 각 사용자에 대한 총 점수를 계산하여 비교
                    int score1 = getTotalScoreByUserAndContest(user1.getId(), contestId);
                    int score2 = getTotalScoreByUserAndContest(user2.getId(), contestId);
                    return Integer.compare(score2, score1); // 높은 점수가 먼저 오도록 내림차순 정렬
                })
                .collect(Collectors.toList());
    }


    @Override
    public Map<Long, List<Integer>> getUserProblemScores(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

        return users.stream().collect(Collectors.toMap(
                User::getId,
                user -> problems.stream()
                        .map(problem -> getScoreByUserAndProblem(user.getId(), problem.getId()))
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public int getScoreByUserAndProblem(Long userId, Long problemId) {
        return submissionRepository.findByUserIdAndProblemId(userId, problemId)
                .map(submission -> Optional.ofNullable(submission.getScore()).orElse(0))
                .orElse(0);
    }

    @Override
    public void deleteContest(Contest contest) {
        contestRepository.delete(contest);
    }

    @Override
    public void removeUserFromContest(Long contestId, Long userId) {
        Optional<Contest> contestOptional = contestRepository.findById(contestId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (contestOptional.isPresent() && userOptional.isPresent()) {
            Contest contest = contestOptional.get();
            User user = userOptional.get();

            contest.getUsers().remove(user);
            contestRepository.save(contest);
        } else {
            throw new IllegalArgumentException("Contest or User not found");
        }
    }

    @Override
    public int getTotalScoreByUserAndContest(Long userId, Long contestId) {
        Integer totalScore = submissionRepository.findTotalScoreByUserAndContest(userId, contestId);
        return totalScore != null ? totalScore : 0;
    }

    @Override
    public List<Contest> searchContestsByName(String searchQuery) {
        return contestRepository.findByNameContainingIgnoreCase(searchQuery);
    }

    @Override
    public Map<Long, List<Integer>> getUserTestCasesPassed(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

        // 각 사용자의 통과한 테스트케이스 수를 저장할 맵
        Map<Long, List<Integer>> userPassedTestCasesMap = new HashMap<>();

        // 각 사용자의 각 문제에 대해 테스트케이스를 비교하여 통과한 수를 계산
        for (User user : users) {
            List<Integer> passedTestCasesPerProblem = new ArrayList<>();

            for (Problem problem : problems) {
                Optional<Submission> submissionOpt = submissionRepository.findByProblemAndUser(problem, user);

                if (submissionOpt.isPresent()) {
                    Submission submission = submissionOpt.get();
                    int passedCount = 0;

                    // 문제의 모든 테스트케이스 가져옴
                    List<TestCase> testCases = problem.getTestCases();

                    for (TestCase testCase : testCases) {
                        // 제출한 결과와 예상 출력 비교
                        if (submission.getResult().equals(testCase.getExpectedOutput())) {
                            passedCount++;
                        }
                    }

                    // 문제당 통과한 테스트케이스 수를 저장
                    passedTestCasesPerProblem.add(passedCount);
                } else {
                    // 제출이 없으면 통과한 테스트케이스 수는 0
                    passedTestCasesPerProblem.add(0);
                }
            }

            // 사용자의 ID를 키로 하고 통과한 테스트케이스 수 리스트를 값으로 저장
            userPassedTestCasesMap.put(user.getId(), passedTestCasesPerProblem);
        }

        return userPassedTestCasesMap;
    }

    @Override
    public Map<Long, List<Boolean>> getUserProblemSolvedStatus(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

        // 각 사용자의 문제 해결 상태를 저장할 맵
        Map<Long, List<Boolean>> userSolvedStatusMap = new HashMap<>();

        for (User user : users) {
            List<Boolean> solvedStatusPerProblem = new ArrayList<>();

            for (Problem problem : problems) {
                Optional<Submission> submissionOpt = submissionRepository.findByProblemAndUser(problem, user);

                if (submissionOpt.isPresent()) {
                    Submission submission = submissionOpt.get();

                    // 모든 테스트케이스를 통과했는지 확인
                    boolean isSolved = Integer.parseInt(submission.getPass_count()) == problem.getTestCases().size();
                    solvedStatusPerProblem.add(isSolved);
                } else {
                    solvedStatusPerProblem.add(false);
                }
            }

            userSolvedStatusMap.put(user.getId(), solvedStatusPerProblem);
        }

        return userSolvedStatusMap;
    }

    @Override
    public Map<Long, List<String>> getUserSubmissionTimes(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

        // 각 사용자의 제출 시간을 저장할 맵
        Map<Long, List<String>> userSubmissionTimesMap = new HashMap<>();

        for (User user : users) {
            List<String> submissionTimesPerProblem = new ArrayList<>();

            for (Problem problem : problems) {
                Optional<Submission> submissionOpt = submissionRepository.findByProblemAndUser(problem, user);

                if (submissionOpt.isPresent()) {
                    Submission submission = submissionOpt.get();
                    submissionTimesPerProblem.add(submission.getSubmittedAt().toString());
                } else {
                    submissionTimesPerProblem.add("N/A");
                }
            }

            userSubmissionTimesMap.put(user.getId(), submissionTimesPerProblem);
        }

        return userSubmissionTimesMap;
    }
    @Override
    public long getParticipantCount(Long contestId) {
        return contestRepository.findById(contestId)
                .map(contest -> contest.getUsers().size())
                .orElse(0);
    }
}
