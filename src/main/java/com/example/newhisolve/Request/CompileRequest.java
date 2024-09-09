package com.example.newhisolve.Request;

import lombok.Getter;

@Getter
public class CompileRequest {
    private Long problemId;
    private String code;
    private String language;

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
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
                "problemId=" + problemId +
                ", code='" + code + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
