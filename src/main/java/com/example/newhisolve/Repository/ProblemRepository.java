package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByContest(Contest contest);
    List<Problem> findByContestId(Long contestId);
}
