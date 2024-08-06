package com.example.newhisolve.Controller;
import com.example.newhisolve.Model.GradingTestCase;
import com.example.newhisolve.Request.CompileRequest;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Service.AssignmentService;
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
    private final AssignmentService assignmentService;

    @PostMapping("/compile")
    public ResponseEntity<List<Map<String, String>>> compileCode(@RequestBody CompileRequest request) {
        Long assignmentId = request.getAssignmentId();
        if (assignmentId == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        List<TestCase> testCases = assignmentService.getTestCasesForAssignment(assignmentId);
        List<Map<String, String>> output = compilerService.compileAndRun(assignmentId, request.getCode(), request.getLanguage(), testCases);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/gradingCompile")
    public ResponseEntity<List<Map<String, String>>> gradingCompileCode(@RequestBody CompileRequest request) {
        Long assignmentId = request.getAssignmentId();
        if (assignmentId == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        List<GradingTestCase> gradingTestCases = assignmentService.getGradingTestCasesForAssignment(assignmentId);
        List<Map<String, String>> output = compilerService.gradingCompileAndRun(assignmentId, request.getCode(), request.getLanguage(), gradingTestCases);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/run")
    public Map<String, String> runCode(@RequestBody CompileRequest request) {
        String output = compilerService.runCode(request.getCode(), request.getLanguage());
        return Map.of("output", output);
    }

    @PostMapping("/runWithInput")
    public Map<String, String> runCodeWithInput(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        String language = (String) request.get("language");
        List<String> inputs = (List<String>) request.get("inputs");
        String output = compilerService.runCodeWithInput(code, language, inputs);
        return Map.of("output", output);
    }
}
