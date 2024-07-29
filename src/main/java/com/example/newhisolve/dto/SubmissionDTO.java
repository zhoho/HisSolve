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
    private String passCount;
}
