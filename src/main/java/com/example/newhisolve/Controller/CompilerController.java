package com.example.newhisolve.Controller;

import com.example.newhisolve.Request.CompileRequest;
import com.example.newhisolve.Model.TestCase;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.CompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/compile")
    public Map<String, String> compileCode(@RequestBody CompileRequest request) {
        Long assignmentId = request.getAssignmentId();
        if (assignmentId == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        List<TestCase> testCases = assignmentService.getTestCasesForAssignment(assignmentId);
        String output = compilerService.compileAndRun(assignmentId, request.getCode(), request.getLanguage(), testCases);
        return Map.of("output", output);
    }

    @PostMapping("/run")
    public Map<String, String> runCode(@RequestBody CompileRequest request) {
        String output = compilerService.runCode(request.getCode(), request.getLanguage());
        return Map.of("output", output);
    }

    @PostMapping("/runWithInput")
    public Map<String, String> runCodeWithInput(@RequestBody CompileRequest request, @RequestParam String input) {
        String output = compilerService.runCodeWithInput(request.getCode(), request.getLanguage(), input);
        return Map.of("output", output);
    }
}
