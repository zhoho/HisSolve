package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.ClassRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(Assignment assignment, Long classId);
    Assignment findById(Long id);
    void submitAssignment(Long assignmentId, String code, String studentUsername);
    List<Submission> findSubmissionsByAssignment(Assignment assignment);
}

