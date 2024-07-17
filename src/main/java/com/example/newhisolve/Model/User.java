package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Builder;

import java.util.List;

@Entity
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
    // private String uniqueId; // 필요에 따라 추가

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

    // Getters and Setters

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHisnetToken() {
        return hisnetToken;
    }

    public void setHisnetToken(String hisnetToken) {
        this.hisnetToken = hisnetToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

}
