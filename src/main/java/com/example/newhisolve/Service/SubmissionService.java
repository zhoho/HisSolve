package com.example.newhisolve.Service;
import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.SavedCode;
import com.example.newhisolve.dto.SubmissionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public interface SubmissionService {

    @Transactional
    Submission saveSubmission(SubmissionDTO submissionDTO);

    @Transactional
    SavedCode saveCode(SubmissionDTO submissionDTO);

    Optional<SavedCode> getSavedCode(Long assignmentId, Long studentId);

    Optional<Submission> getSubmission(Long submissionId);

    List<Submission> findByAssignmentId(Long assignmentId);

    @Transactional
    void deleteSubmissionsByAssignmentId(Long assignmentId);

    List<Submission> findByAssignmentAndStudent(Long assignmentId, Long studentId);

    List<Submission> findSubmissionsByAssignment(Assignment assignment);

    String getGradingTestcaseCount(Long assignmentId);
}
