package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByCode(String code);
    List<Class> findByProfessor(User professor);
    List<Class> findByStudentsContaining(User student);
}
