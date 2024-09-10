package com.example.newhisolve.Model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
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
    @JoinColumn(name = "contest_id")
    private Contest contest;
}
