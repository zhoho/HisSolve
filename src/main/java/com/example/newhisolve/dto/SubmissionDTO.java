package com.example.newhisolve.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubmissionDTO {
    private Long assignmentId;
    private Long studentId;
    private String code;
    private String language;
    private LocalDateTime lastSavedDate;
    private String totalCount;
    private String passCount;

    public SubmissionDTO() {}

    public SubmissionDTO(Long assignmentId, Long studentId, String code, String language, LocalDateTime lastSavedDate, String totalCount, String passCount) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.code = code;
        this.language = language;
        this.lastSavedDate = lastSavedDate;
        this.totalCount = totalCount;
        this.passCount = passCount;
    }
}
