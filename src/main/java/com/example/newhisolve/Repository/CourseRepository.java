package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Contest, Long> {
    Optional<Contest> findByCode(String code);
    List<Contest> findByProfessor(User professor);
    List<Contest> findByStudentsContaining(User student);
}
