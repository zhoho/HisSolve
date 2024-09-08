package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private String code;
    private String language;
    private String description;
    private String problemCount;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User professor;

    // ContestUser와의 관계 설정 (OneToMany)
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestUser> contestUsers = new ArrayList<>();

    // getStudents 메소드 추가
    public List<User> getStudents() {
        List<User> students = new ArrayList<>();
        for (ContestUser contestUser : contestUsers) {
            students.add(contestUser.getUser());
        }
        return students;
    }
}
