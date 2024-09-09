package com.example.newhisolve.Service;
import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.User;
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
        // 기존 경연을 데이터베이스에서 불러와서 수정사항을 반영
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
        Contest contest = findById(contestId);
        List<User> users = contest.getUsers();

        return users.stream()
                .sorted(Comparator.comparingInt(user -> getTotalScoreByUserAndContest(((User) user).getId(), contestId)).reversed())
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
}
