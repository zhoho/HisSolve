package com.example.newhisolve.Model;
import jakarta.persistence.*;
import junit.framework.TestResult;
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
    private String gradingTestcaseCount;

    @ElementCollection
    private List<TestCase> testCases;

    @ElementCollection
    private List<GradingTestCase> gradingTestCases;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
