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
        // assignmentId를 사용하여 해당 과제의 테스트 케이스를 불러옵니다.
        List<TestCase> testCases = getTestCasesForAssignment(assignmentId);

        try {
            // 임시 파일 생성 및 저장
            String fileName = "TempCode";
            String fileExtension = getFileExtension(language);
            String filePath = fileName + fileExtension;

            // 코드 파일로 저장
            saveCodeToFile(filePath, code);

            // Docker 명령어 실행
            String dockerImage = getDockerImage(language);
            String command = String.format("docker run --rm -v $(pwd):/app %s %s", dockerImage, filePath);

            // 결과 변수
            StringBuilder results = new StringBuilder();

            for (TestCase testCase : testCases) {
                Process process = Runtime.getRuntime().exec(command);

                // 입력 값 전달
                process.getOutputStream().write(testCase.getInput().getBytes());
                process.getOutputStream().flush();
                process.getOutputStream().close();

                // 실행 결과 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();

                // 결과 비교
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
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private String getDockerImage(String language) {
        switch (language) {
            case "python": return "python:3.8-slim";
            case "java": return "openjdk:11-slim";
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private List<TestCase> getTestCasesForAssignment(Long assignmentId) {
        // assignmentId를 사용하여 데이터베이스에서 해당 과제의 테스트 케이스를 가져옵니다.
        // 이 부분은 실제 데이터베이스 조회 로직으로 구현해야 합니다.
        // 여기에 임시로 더미 데이터를 반환하도록 하겠습니다.
        return List.of(new TestCase("input1", "expectedOutput1"), new TestCase("input2", "expectedOutput2"));
    }
}
