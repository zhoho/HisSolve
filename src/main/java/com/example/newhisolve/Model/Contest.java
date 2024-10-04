package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "contest")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String code;
    private String language;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String problemCount;

    // Many-to-One 관계: 하나의 Contest는 한 명의 User(관리자)를 가짐
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    // Many-to-Many 관계: 하나의 Contest는 여러 명의 User가 참여할 수 있음
    @ManyToMany
    @JoinTable(
            name = "contest_user",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    // 기본 생성자
    public Contest() {
    }

    // 필드들을 초기화하는 생성자, 필요한 경우 추가 가능
    public Contest(String name, String code, String language, String description, String problemCount, User admin, LocalDateTime startDate, LocalDateTime dueDate) {
        this.name = name;
        this.code = code;
        this.language = language;
        this.description = description;
        this.problemCount = problemCount;
        this.admin = admin;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public String getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate)) {
            return "진행 예정";
        } else if (now.isAfter(dueDate)) {
            return "종료됨";
        } else {
            return "진행 중";
        }
    }

    public String getDuration() {
        return startDate.toLocalDate() + " ~ " + dueDate.toLocalDate();
    }
}
