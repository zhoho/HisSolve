package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Problem;
import com.example.newhisolve.Model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByContest(Contest contest);
    List<Problem> findByContestId(Long contestId);
    @Query("SELECT p.testcaseCount FROM Problem p WHERE p.id = :id")
    Integer findTestcaseCountById(@Param("id") Long id);}
