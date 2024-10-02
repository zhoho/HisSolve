package com.example.newhisolve.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "saved_code", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"problem_id", "user_id"})
})
public class SavedCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String code;

    @Column(name = "last_saved_date")
    private LocalDateTime lastSavedDate;

    private String language;

    @Override
    public String toString() {
        return "SavedCode{" +
                "id=" + id +
                ", problem=" + (problem != null ? problem.getId() : "null") +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", code='" + code + '\'' +
                ", lastSavedDate=" + lastSavedDate +
                ", language='" + language + '\'' +
                '}';
    }
}
