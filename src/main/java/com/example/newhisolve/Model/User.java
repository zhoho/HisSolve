package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    @Column(columnDefinition = "TEXT")
    private String hisnetToken;
    private String email;
    private String department;


    private String uniqueId;
    @OneToMany(mappedBy = "professor")
    private List<Course> courses;

    @ManyToMany(mappedBy = "students")
    private List<Course> enrolledCourses;


    @Builder
    public User(String username, String password, String role, String hisnetToken, String uniqueId, String email, String department) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.hisnetToken = hisnetToken;
        this.uniqueId = uniqueId;
        this.email = email;
        this.department = department;
    }

    public User(String hisnetToken) {
        this.hisnetToken = hisnetToken;
    }

    public User() {
    }

    public void update(User user) {
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setRole(user.getRole());
        this.setHisnetToken(user.getHisnetToken());
        this.setEmail(user.getEmail());
        this.setDepartment(user.getDepartment());
        this.setUniqueId(user.getUniqueId());
    }
}
