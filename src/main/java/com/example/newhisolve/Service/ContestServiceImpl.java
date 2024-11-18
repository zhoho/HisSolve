package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final SavedCodeRepository savedCodeRepository;
    private final ContestUserRepository contestUserRepository;
    @Override
    public Contest createContest(Contest contest, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        contest.setAdmin(admin);
        contest.setCode(UUID.randomUUID().toString().substring(0, 5));
        return contestRepository.save(contest);
    }

    @Override
    public void joinContest(String code, String userUsername) {
        Contest contest = contestRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        User user = userRepository.findByUsername(userUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!contest.getUsers().contains(user)) {
            contest.getUsers().add(user);
            contestRepository.save(contest);
        } else {
            throw new RuntimeException("이미 이 콘테스트에 참가하셨습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Contest findById(Long id) {
        return contestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contest> findByUser(User user) {
        if (user.getRole().equals("ADMIN")) {
            return contestRepository.findByAdmin(user);
        } else {
            return contestRepository.findByUsersContaining(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Problem> findProblemsByContest(Contest contest) {
        return problemRepository.findByContest(contest);
    }

    @Override
    public void updateContest(Contest contest) {
        Contest existingContest = contestRepository.findById(contest.getId())
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        existingContest.setName(contest.getName());
        existingContest.setCode(contest.getCode());
        existingContest.setDescription(contest.getDescription());
        existingContest.setLanguage(contest.getLanguage());
        existingContest.setLanguageStatic(contest.isLanguageStatic());
        existingContest.setStartDate(contest.getStartDate());
        existingContest.setDueDate(contest.getDueDate());
        contestRepository.save(existingContest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contest> findContestsByAdmin(User admin) {
        return contestRepository.findByAdmin(admin);
    }

    @Override
    public List<User> getSortedUsersByTotalScore(Long contestId) {
        Contest contest = findById(contestId);
        List<User> users = contest.getUsers();

        return users.stream()
                .sorted((user1, user2) -> {
                    int score1 = getTotalScoreByUserAndContest(user1.getId(), contestId);
                    int score2 = getTotalScoreByUserAndContest(user2.getId(), contestId);
                    return Integer.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public int getScoreByUserAndProblem(Long userId, Long problemId) {
        return submissionRepository.findByUserIdAndProblemId(userId, problemId)
                .map(submission -> Optional.ofNullable(submission.getScore()).orElse(0))
                .orElse(0);
    }


    @Override
    public void deleteContest(Contest contest) {
        List<Problem> problems = problemRepository.findByContest(contest);
        // 1. submission 삭제
        submissionRepository.deleteByContest(contest);
        // 2. 각 problem에 대한 saved_code 삭제
        for (Problem problem : problems) {
            savedCodeRepository.deleteByProblem(problem);
        }
        // 3. problem 삭제
        problemRepository.deleteByContest(contest);
        // 4. contest_user 삭제
        contestUserRepository.deleteByContest(contest);
        // 5. contest 삭제
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
    @Transactional(readOnly = true)
    public int getTotalScoreByUserAndContest(Long userId, Long contestId) {
        Integer totalScore = submissionRepository.findTotalScoreByUserAndContest(userId, contestId);
        return totalScore != null ? totalScore : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contest> searchContestsByName(String searchQuery) {
        return contestRepository.findByNameContainingIgnoreCase(searchQuery);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Integer>> getUserTestCasesPassed(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

        Map<Long, List<Integer>> userPassedTestCasesMap = new HashMap<>();

        for (User user : users) {
            List<Integer> passedTestCasesPerProblem = new ArrayList<>();
            for (Problem problem : problems) {
                Optional<Submission> submissionOpt = submissionRepository.findByProblemAndUser(problem, user);

                if (submissionOpt.isPresent()) {
                    Submission submission = submissionOpt.get();
                    int passedCount = 0;
                    List<TestCase> testCases = problem.getTestCases();

                    if (submission.getScore() == 100) {
                        passedCount = testCases.size();
                    } else {
                        for (TestCase testCase : testCases) {
                            if (submission.getResult().equals(testCase.getExpectedOutput())) {
                                passedCount++;
                            }
                        }
                    }

                    passedTestCasesPerProblem.add(passedCount);
                } else {
                    passedTestCasesPerProblem.add(0);
                }
            }

            userPassedTestCasesMap.put(user.getId(), passedTestCasesPerProblem);
        }

        return userPassedTestCasesMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Boolean>> getUserProblemSolvedStatus(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

        Map<Long, List<Boolean>> userSolvedStatusMap = new HashMap<>();

        for (User user : users) {
            List<Boolean> solvedStatusPerProblem = new ArrayList<>();
            for (Problem problem : problems) {
                Optional<Submission> submissionOpt = submissionRepository.findByProblemAndUser(problem, user);

                if (submissionOpt.isPresent()) {
                    Submission submission = submissionOpt.get();
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
    @Transactional(readOnly = true)
    public Map<Long, List<String>> getUserSubmissionTimes(Long contestId) {
        List<User> users = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"))
                .getUsers();
        List<Problem> problems = problemRepository.findByContestId(contestId);

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
    @Transactional(readOnly = true)
    public long getParticipantCount(Long contestId) {
        return contestRepository.findById(contestId)
                .map(contest -> contest.getUsers().size())
                .orElse(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> getParticipantCountForProblems(Long contestId) {
        List<Problem> problems = problemRepository.findByContestId(contestId);
        Map<Long, Long> problemParticipantCounts = new HashMap<>();

        for (Problem problem : problems) {
            long participantCount = savedCodeRepository.countDistinctUsersByProblemId(problem.getId());
            problemParticipantCounts.put(problem.getId(), participantCount);
        }

        return problemParticipantCounts;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public String getProblemStatusForUser(Problem problem, User user) {
        Optional<Submission> submission = submissionRepository.findByUserAndProblem(user, problem);
        if (submission.isPresent()) {
            return "제출 완료";
        }

        Optional<SavedCode> savedCode = savedCodeRepository.findByUserAndProblem(user, problem);
        if (savedCode.isPresent()) {
            return "진행 중";
        }

        return "미진행";
    }

    @Override
    public Map<Long, Integer> getProblemSolvedUserCounts(Long contestId) {
        List<Problem> problems = problemRepository.findByContestId(contestId);

        Map<Long, Integer> problemSolvedUserCounts = new HashMap<>();

        for (Problem problem : problems) {
            int count = submissionRepository.countDistinctUsersByProblemAndScore(problem.getId(), 100);
            problemSolvedUserCounts.put(problem.getId(), count);
        }

        return problemSolvedUserCounts;
    }

    @Override
    @Transactional(readOnly = true)
    public Contest getContestByProblemId(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("이 Id에 맞는 Problem은 없음: " + problemId));
        return problem.getContest();
    }
}
