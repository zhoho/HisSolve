package com.example.newhisolve.Model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime dueDate;
    private LocalDateTime lastModifiedDate;
    private String testcaseCount;

    @ElementCollection
    private List<TestCase> testCases;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
