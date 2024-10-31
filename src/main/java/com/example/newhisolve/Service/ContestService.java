package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.User;

import java.util.List;
import java.util.Map;

public interface ContestService {
    Contest createContest(Contest contest, String adminUsername);

    void joinContest(String code, String userUsername);

    Contest findById(Long id);

    List<Contest> findByUser(User user);

    List<Problem> findProblemsByContest(Contest contest);

    void updateContest(Contest contest);

    List<Contest> findContestsByAdmin(User admin);

    List<User> getSortedUsersByTotalScore(Long contestId);

    Map<Long, List<Integer>> getUserProblemScores(Long contestId);

    int getScoreByUserAndProblem(Long userId, Long problemId);

    void deleteContest(Contest contest);

    void removeUserFromContest(Long contestId, Long userId);

    int getTotalScoreByUserAndContest(Long userId, Long contestId);

    List<Contest> searchContestsByName(String searchQuery);
    Map<Long, List<Integer>> getUserTestCasesPassed(Long contestId);
    Map<Long, List<Boolean>> getUserProblemSolvedStatus(Long contestId);

    Map<Long, List<String>> getUserSubmissionTimes(Long contestId);
    long getParticipantCount(Long contestId);
    Map<Long, Long> getParticipantCountForProblems(Long contestId);
    User findUserByUsername(String username);
    String getProblemStatusForUser(Problem problem, User user);
    Map<Long, Integer> getProblemSolvedUserCounts(Long contestId);
    Contest getContestByProblemId(Long problemId);
}

