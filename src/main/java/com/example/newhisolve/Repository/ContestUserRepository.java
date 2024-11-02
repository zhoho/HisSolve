package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.ContestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContestUserRepository extends JpaRepository<ContestUser,Integer> {
    void deleteByContest(Contest contest);
}
