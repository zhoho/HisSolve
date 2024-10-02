package com.example.newhisolve.Service;

import com.example.newhisolve.Model.GradingTestCase;
import com.example.newhisolve.Model.TestCase;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public interface CompilerService {

    List<Map<String, String>> compileAndRun(Long assignmentId, String code, String language, List<TestCase> testCases);

    List<Map<String, String>> gradingCompileAndRun(Long assignmentId, String code, String language, List<TestCase> testCases);

    String runCode(String code, String language);

    String runCodeWithInput(String code, String language, List<String> inputs);

    void saveCodeToFile(String filePath, String code) throws Exception;

    String getFileExtension(String language);

    String getDockerImage(String language);

    String getDockerCommand(String language, String filePath, String hostPath);

    void deleteDirectory(File directory);
}
