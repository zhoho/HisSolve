package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.GradingTestCase;
import com.example.newhisolve.Request.CompileRequest;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Service.ProblemService;
import com.example.newhisolve.Service.CompilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CompilerController {

    private final CompilerService compilerService;
    private final ProblemService problemService;

    // 코드 컴파일 및 실행
    @PostMapping("/compile")
    public ResponseEntity<List<Map<String, String>>> compileCode(@RequestBody CompileRequest request) {
        Long problemId = request.getProblemId();
        if (problemId == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }

        // 모든 테스트케이스 (히든 포함) 가져오기
        List<TestCase> testCases = problemService.getTestCasesForProblem(problemId);

        // 코드 컴파일 및 실행
        List<Map<String, String>> output = compilerService.compileAndRun(problemId, request.getCode(), request.getLanguage(), testCases);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/submitWithHidden")
    public ResponseEntity<List<Map<String, String>>> gradingCompileCode(@RequestBody CompileRequest request) {
        Long problemId = request.getProblemId();
        if (problemId == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        List<TestCase> gradingTestCases = problemService.getTestCasesForProblem(problemId);
        List<Map<String, String>> output = compilerService.gradingCompileAndRun(problemId, request.getCode(), request.getLanguage(), gradingTestCases);
        return ResponseEntity.ok(output);
    }

    // 코드 실행 (테스트케이스 없는 일반 실행)
    @PostMapping("/run")
    public Map<String, String> runCode(@RequestBody CompileRequest request) {
        String output = compilerService.runCode(request.getCode(), request.getLanguage());
        return Map.of("output", output);
    }

    // 코드 실행 (입력값 포함)
    @PostMapping("/runWithInput")
    public Map<String, String> runCodeWithInput(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        String language = (String) request.get("language");
        List<String> inputs = (List<String>) request.get("inputs");
        String output = compilerService.runCodeWithInput(code, language, inputs);
        return Map.of("output", output);
    }
}
