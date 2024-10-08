package com.example.newhisolve.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class SubmissionDTO {
    private Long problemId;
    private Long userId;
    private String code;
    private LocalDateTime lastSavedDate;
    private String language;
    private String result;
    private String passCount;
    private int totalScore;
}
