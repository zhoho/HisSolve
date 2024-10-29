package com.example.newhisolve.Service;

import com.example.newhisolve.Model.*;
import com.example.newhisolve.Repository.ProblemRepository;
import com.example.newhisolve.Repository.ContestRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.newhisolve.Repository.SavedCodeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final SavedCodeRepository savedCodeRepository;

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
        contest.getUsers().add(user);
        contestRepository.save(contest);
    }

    @Override
    public Contest findById(Long id) {
        return contestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
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
        existingContest.setCode(contest.getCode());
        existingContest.setDescription(contest.getDescription());
        contestRepository.save(existingContest);
    }

    @Override
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
    public long getParticipantCount(Long contestId) {
        return contestRepository.findById(contestId)
                .map(contest -> contest.getUsers().size())
                .orElse(0);
    }

    @Override
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
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
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
}
