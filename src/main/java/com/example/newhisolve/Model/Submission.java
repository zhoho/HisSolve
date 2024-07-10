package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Submission {

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    private String code;
    private String result;
    private LocalDateTime submittedAt;

    // Getters and Setters
}
