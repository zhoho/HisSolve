package com.example.newhisolve.Request;

import lombok.Getter;

@Getter
public class CompileRequest {
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
