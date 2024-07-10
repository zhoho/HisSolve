package com.example.newhisolve.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Assignment {

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTestCases(String testCases) {
        this.testCases = testCases;
    }

    public void setCourseEntity(Course classEntity) {
        this.course = course;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String testCases;  // JSON format


    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;



    // Getters and Setters
}
