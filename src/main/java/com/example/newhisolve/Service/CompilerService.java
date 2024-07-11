package com.example.newhisolve.Service;

import com.example.newhisolve.Model.TestCase;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class CompilerService {

    public String compileAndRun(Long assignmentId, String code, String language) {
        List<TestCase> testCases = getTestCasesForAssignment(assignmentId);

        try {
            String fileName = "TempCode";
            String fileExtension = getFileExtension(language);
            String filePath = fileName + fileExtension;

            saveCodeToFile(filePath, code);

            String compileCommand = getCompileCommand(language, filePath);
            String runCommand = getRunCommand(language, fileName);

            StringBuilder results = new StringBuilder();

            for (TestCase testCase : testCases) {
                if (!compileCommand.isEmpty()) {
                    Process compileProcess = Runtime.getRuntime().exec(new String[]{"sh", "-c", compileCommand});
                    compileProcess.waitFor();
                }

                Process runProcess = Runtime.getRuntime().exec(new String[]{"sh", "-c", runCommand});

                runProcess.getOutputStream().write(testCase.getInput().getBytes());
                runProcess.getOutputStream().flush();
                runProcess.getOutputStream().close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();

                if (output.toString().trim().equals(testCase.getExpectedOutput().trim())) {
                    results.append("Pass\n");
                } else {
                    results.append("Fail\n");
                }
            }

            return results.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private void saveCodeToFile(String filePath, String code) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(code);
        }
    }

    private String getFileExtension(String language) {
        switch (language) {
            case "python": return ".py";
            case "java": return ".java";
            case "c": return ".c";
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private String getCompileCommand(String language, String filePath) {
        switch (language) {
            case "python": return "";
            case "java": return String.format("docker run --rm -v $(pwd):/app mycompiler-java sh -c \"javac %s\"", filePath);
            case "c": return String.format("docker run --rm -v $(pwd):/app mycompiler-c sh -c \"gcc -o %s %s\"", filePath.replace(".c", ""), filePath);
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private String getRunCommand(String language, String fileName) {
        switch (language) {
            case "python": return String.format("docker run --rm -v $(pwd):/app mycompiler-python python %s.py", fileName);
            case "java": return String.format("docker run --rm -v $(pwd):/app mycompiler-java java %s", fileName);
            case "c": return String.format("docker run --rm -v $(pwd):/app mycompiler-c ./%s", fileName);
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private List<TestCase> getTestCasesForAssignment(Long assignmentId) {
        // assignmentId를 사용하여 데이터베이스에서 해당 과제의 테스트 케이스를 가져옵니다.
        // 이 부분은 실제 데이터베이스 조회 로직으로 구현해야 합니다.
        // 여기에 임시로 더미 데이터를 반환하도록 하겠습니다.
        return List.of(new TestCase(), new TestCase());
    }
}
