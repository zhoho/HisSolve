package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest; // Course와의 관계 추가

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String code;

    private String result;

    private String pass_count;

    private LocalDateTime submittedAt;

    private String language;

    private int score;

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", assignment=" + (problem != null ? problem.getId() : "null") +
                ", student=" + (student != null ? student.getUsername() : "null") +
                ", course=" + (contest != null ? contest.getId() : "null") +
                ", code='" + code + '\'' +
                ", result='" + result + '\'' +
                ", submittedAt=" + submittedAt +
                ", language='" + language + '\'' +
                ", score=" + score +
                '}';
    }
}
