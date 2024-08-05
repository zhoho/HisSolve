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
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course; // Course와의 관계 추가

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String code;

    private String result;

    private String pass_count;

    private LocalDateTime submittedAt;

    private String language;

    private int score; // 학생별 점수 컬럼 추가

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", assignment=" + (assignment != null ? assignment.getId() : "null") +
                ", student=" + (student != null ? student.getUsername() : "null") +
                ", course=" + (course != null ? course.getId() : "null") +
                ", code='" + code + '\'' +
                ", result='" + result + '\'' +
                ", submittedAt=" + submittedAt +
                ", language='" + language + '\'' +
                ", score=" + score +
                '}';
    }
}
