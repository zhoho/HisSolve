package com.example.newhisolve.Service;

import com.example.newhisolve.Model.TestCase;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class CompilerService {

    public String compileAndRun(Long assignmentId, String code, String language, List<TestCase> testCases) {
        try {
            // 임시 디렉토리 생성
            File tempDir = new File("tempDir");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }

            // 임시 파일 생성 및 저장
            String fileName = "TempCode";
            String fileExtension = getFileExtension(language);
            String filePath = tempDir.getAbsolutePath() + "/" + fileName + fileExtension;

            // 코드 파일로 저장
            saveCodeToFile(filePath, code);
            System.out.println("Code saved to file: " + filePath);

            // Docker 명령어 실행
            String dockerImage = getDockerImage(language);
            String command = getDockerCommand(language, fileName + fileExtension, tempDir.getAbsolutePath());

            // 결과 변수
            StringBuilder results = new StringBuilder();

            for (TestCase testCase : testCases) {
                int testCount = 0;
                testCount ++; //test case count up
                System.out.println("Executing command: " + command);

                ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                if (testCase.getInput() != null) {
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                        writer.write(testCase.getInput());
                        writer.newLine();
                        writer.flush();
                    }
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();

                process.waitFor();

                String actucalInput = testCase.getInput();
                String actualOutput = output.toString().trim();
                String expectedOutput = testCase.getExpectedOutput().trim();


                // 디버깅 출력 추가
                System.out.println("Test Case - Input: " + testCase.getInput());
                System.out.println("Expected Output: " + expectedOutput);
                System.out.println("Actual Output: " + actualOutput);


                if (actualOutput.equals(expectedOutput)) {
                    results.append("테스트 "+testCount+" - 통과\n입력값 〉 " + actucalInput + "\n기댓값 〉 " + expectedOutput + "\n실행 결과 〉 " + "실행한 결괏값 " + actualOutput + "이 기댓값 " + expectedOutput + "과 일치합니다!\n" );
                } else {
                    results.append("테스트 "+testCount+" - 실패\n입력값 〉 " + actucalInput + "\n기댓값 〉 " + expectedOutput + "\n실행 결과 〉 " + "실행한 결괏값 " + actualOutput + "이 기댓값 " + expectedOutput + "과 다릅니다!\n" );
                }
            }

            deleteDirectory(tempDir);

            return results.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String runCode(String code, String language) {
        return runCodeWithInput(code, language, null);
    }

    public String runCodeWithInput(String code, String language, List<String> inputs) {
        try {
            // 임시 디렉토리 생성
            File tempDir = new File("tempDir");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }

            // 임시 파일 생성 및 저장
            String fileName = "TempCode";
            String fileExtension = getFileExtension(language);
            String filePath = tempDir.getAbsolutePath() + "/" + fileName + fileExtension;

            // 코드 파일로 저장
            saveCodeToFile(filePath, code);

            // Docker 명령어 실행
            String dockerImage = getDockerImage(language);
            String command = getDockerCommand(language, fileName + fileExtension, tempDir.getAbsolutePath());

            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            if (inputs != null && !inputs.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    for (String input : inputs) {
                        writer.write(input);
                        writer.newLine();
                    }
                    writer.flush();
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();

            process.waitFor();

            // 임시 디렉토리 삭제
            deleteDirectory(tempDir);

            return output.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private void saveCodeToFile(String filePath, String code) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(code);
            System.out.println("Code written: " + code);
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

    private String getDockerImage(String language) {
        switch (language) {
            case "python": return "python:3.8-slim";
            case "java": return "openjdk:11-slim";
            case "c": return "gcc:latest";
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private String getDockerCommand(String language, String filePath, String hostPath) {
        switch (language) {
            case "python": return String.format("docker run --rm -i -v %s:/app -w /app python:3.8-slim python %s", hostPath, filePath);
            case "java": return String.format("docker run --rm -i -v %s:/app -w /app openjdk:11-slim sh -c 'javac %s && java TempCode'", hostPath, filePath);
            case "c": return String.format("docker run --rm -i -v %s:/app -w /app gcc:latest sh -c 'gcc -o TempCode %s && ./TempCode'", hostPath, filePath);
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}
