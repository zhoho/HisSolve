package com.example.newhisolve.Service;
import com.example.newhisolve.Model.Problem;
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

    Optional<SavedCode> getSavedCode(Long problemId, Long userId);

    Optional<Submission> getSubmission(Long submissionId);

    List<Submission> findByProblemId(Long problemId);

    @Transactional
    void deleteSubmissionsByProblemId(Long problemId);

    List<Submission> findByProblemAndUser(Long problemId, Long userId);

    List<Submission> findSubmissionsByProblem(Problem problem);

    String getGradingTestcaseCount(Long problemId);
}
