package com.example.newhisolve.Repository;

import com.example.newhisolve.Model.SavedCode;
import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedCodeRepository extends JpaRepository<SavedCode, Long> {
    Optional<SavedCode> findByAssignmentAndStudent(Assignment assignment, User student);
}
