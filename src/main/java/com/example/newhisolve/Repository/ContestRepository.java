package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    Optional<Contest> findByCode(String code);
    List<Contest> findByAdmin(User admin);
    List<Contest> findByUsersContaining(User user);
    List<Contest> findByNameContainingIgnoreCase(String name);
}
