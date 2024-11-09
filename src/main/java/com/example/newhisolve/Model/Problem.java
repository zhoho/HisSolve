package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private List<TestCase> testCases;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Column(name = "testcase_count")
    private Integer testcaseCount;

    public String getProblemDueDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dueDate.format(formatter);
    }
}
