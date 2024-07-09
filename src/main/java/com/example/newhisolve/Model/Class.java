//package com.example.newhisolve.Model;
//
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "classes")  // 'Class'가 예약어이므로 테이블 이름을 'classes'로 지정
//public class Class {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private String code;
//
//    @ManyToOne
//    @JoinColumn(name = "professor_id")
//    private User professor;
//
//    @ManyToMany
//    @JoinTable(
//            name = "class_student",
//            joinColumns = @JoinColumn(name = "class_id"),
//            inverseJoinColumns = @JoinColumn(name = "student_id")
//    )
//    private List<User> students = new ArrayList<>();
//
//    // Getters and Setters
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public User getProfessor() {
//        return professor;
//    }
//
//    public void setProfessor(User professor) {
//        this.professor = professor;
//    }
//
//    public List<User> getStudents() {
//        return students;
//    }
//
//    public void setStudents(List<User> students) {
//        this.students = students;
//    }
//}
