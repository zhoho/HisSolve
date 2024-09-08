package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "saved_code", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assignment_id", "student_id"})
})public class SavedCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Problem assignment;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

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
                ", assignment=" + (assignment != null ? assignment.getId() : "null") +
                ", student=" + (student != null ? student.getUsername() : "null") +
                ", code='" + code + '\'' +
                ", lastSavedDate=" + lastSavedDate +
                ", language='" + language + '\'' +
                '}';
    }
}
