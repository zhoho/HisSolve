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
    private User admin;
    // ContestUser와의 관계 설정 (OneToMany)
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestUser> contestUsers = new ArrayList<>();

    // getUsers 메소드 추가
    public List<User> getUsers() {  // 메소드명과 변수명 변경
        List<User> users = new ArrayList<>();
        for (ContestUser contestUser : contestUsers) {
            users.add(contestUser.getUser());
        }
        return users;
    }
}
