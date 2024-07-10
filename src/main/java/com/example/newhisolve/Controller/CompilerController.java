package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.CompileRequest;
import com.example.newhisolve.Service.CompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @PostMapping("/compile")
    public Map<String, String> compileCode(@RequestBody CompileRequest request) {
        String output = compilerService.compileAndRun(request.getAssignmentId(), request.getCode(), request.getLanguage());
        return Map.of("output", output);
    }

}
