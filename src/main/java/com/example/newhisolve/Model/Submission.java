package com.example.newhisolve.Model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
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
    private User user;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

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
                ", problem=" + (problem != null ? problem.getId() : "null") +
                ", user=" + (user != null ? user.getUsername() : "null") +  // student를 user로 변경
                ", contest=" + (contest != null ? contest.getId() : "null") +  // course를 contest로 변경
                ", code='" + code + '\'' +
                ", result='" + result + '\'' +
                ", submittedAt=" + submittedAt +
                ", language='" + language + '\'' +
                ", score=" + score +
                '}';
    }
}
