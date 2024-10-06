package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByProblemAndUser(Problem problem, User user);
    List<Submission> findByProblemId(Long problemId);
    void deleteByProblemId(Long problemId);
    void deleteByUserId(Long userId);
    List<Submission> findByProblemIdAndUserId(Long problemId, Long userId);
    List<Submission> findByProblem(Problem problem);


    @Query("SELECT SUM(s.score) FROM Submission s WHERE s.user.id = :userId AND s.contest.id = :contestId")
    Integer findTotalScoreByUserAndContest(@Param("userId") Long userId, @Param("contestId") Long contestId);

    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId AND s.problem.id = :problemId")
    Optional<Submission> findByUserIdAndProblemId(Long userId, Long problemId);
}
