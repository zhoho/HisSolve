package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Service.ProblemService;
import com.example.newhisolve.Service.SubmissionService;
import com.example.newhisolve.dto.SubmissionDTO;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.SavedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final ProblemService problemService;

    // 코드 저장
    @PostMapping("/saveCode")
    public ResponseEntity<?> saveCode(@RequestBody SubmissionDTO submissionDTO) {
        try {
            SavedCode savedCode = submissionService.saveCode(submissionDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("lastSavedDate", savedCode.getLastSavedDate());
            response.put("message", "Code saved successfully!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 저장된 코드 가져오기
    @GetMapping("/getSavedCode")
    @ResponseBody
    public ResponseEntity<String> getSavedCode(@RequestParam Long problemId, @RequestParam Long userId) {
        Optional<SavedCode> savedCodeOptional = submissionService.getSavedCode(problemId, userId);
        if (savedCodeOptional.isPresent()) {
            SavedCode savedCode = savedCodeOptional.get();
            return ResponseEntity.ok(savedCode.getCode());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No saved code found");
        }
    }

    @GetMapping("/TestcaseCount")
    @ResponseBody
    public int TestcaseCount(@RequestParam("problemId") Long id) {
        return problemService.getProblemCountById(id);
    }

    // 코드 제출 및 채점
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmissionDTO submissionDTO) {
        int passCount = Integer.parseInt(submissionDTO.getPassCount());
        List<TestCase> allTestCases = submissionService.getTestCases(submissionDTO.getProblemId());
        int totalTestCases = allTestCases.size(); // 전체 테스트케이스 개수

            submissionDTO.setPassCount(String.valueOf(passCount));
            // 제출 결과 저장
            Submission savedSubmission = submissionService.saveSubmission(submissionDTO);

            // 통과한 테스트케이스 개수 및 결과 반환
            Map<String, Object> response = new HashMap<>();
            response.put("submissionId", savedSubmission.getId());
            response.put("passCount", passCount);
            response.put("totalTestCases", totalTestCases);
            response.put("message", "Submission successful!");

            return ResponseEntity.ok(response);
        }
}
