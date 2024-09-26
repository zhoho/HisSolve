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

    @ElementCollection
    private List<TestCase> testCases; // 일반 테스트케이스와 히든테스트케이스를 하나의 리스트로 관리

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Column(name = "testcase_count")
    private Integer testcaseCount;

}
