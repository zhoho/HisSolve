package com.example.newhisolve.Model;

import lombok.Getter;

@Getter
public class CompileRequest {
    // Getters and Setters
    private Long assignmentId;
    private String code;
    private String language;

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "CompileRequest{" +
                "assignmentId=" + assignmentId +
                ", code='" + code + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
