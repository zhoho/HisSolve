package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.SavedCode;
import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SavedCodeRepository extends JpaRepository<SavedCode, Long> {
    Optional<SavedCode> findByProblemAndUser(Problem problem, User user);
    void deleteByUserId(Long userId);


    @Query("SELECT COUNT(DISTINCT sc.user) FROM SavedCode sc WHERE sc.problem.id = :problemId")
    long countDistinctUsersByProblemId(@Param("problemId") Long problemId);

    Optional<SavedCode> findByUserAndProblem(User user, Problem problem);
}




