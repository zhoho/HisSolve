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
}
