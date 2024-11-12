package com.example.newhisolve.Service;
import com.example.newhisolve.Model.TestCase;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class CompilerServiceImpl implements CompilerService {

    @Override
    public List<Map<String, String>> compileAndRun(Long assignmentId, String code, String language, List<TestCase> testCases) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            String sharedDir = "/tmp/tempDir_" + UUID.randomUUID();
            File tempDir = new File(sharedDir);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }

            String fileName = "TempCode";
            String fileExtension = getFileExtension(language);
            String filePath = tempDir.getAbsolutePath() + "/" + fileName + fileExtension;

            saveCodeToFile(filePath, code);
            System.out.println("Code saved to file: " + filePath);

            String dockerImage = getDockerImage(language);
            String command = getDockerCommand(language, fileName + fileExtension, tempDir.getAbsolutePath());

            int testCount = 0;
            for (TestCase testCase : testCases) {
                testCount++;
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

                String actualInput = testCase.getInput();
                String actualOutput = output.toString().trim().replaceAll("\\s+", "");
                String expectedOutput = testCase.getExpectedOutput().trim().replaceAll("\\s+", "");

                Map<String, String> result = new HashMap<>();
                result.put("testNumber", "테스트 " + testCount);
                result.put("status", actualOutput.equals(expectedOutput) ? "통과" : "실패");
                result.put("input", actualInput);
                result.put("expectedOutput", testCase.getExpectedOutput());
                result.put("actualOutput", output.toString().trim());
                result.put("isHidden", String.valueOf(testCase.isHidden() ? 1 : 0));
                results.add(result);
            }
            deleteDirectory(tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public List<Map<String, String>> gradingCompileAndRun(Long assignmentId, String code, String language, List<TestCase> gradingTestCases) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            String sharedDir = "/tmp/tempDir_" + UUID.randomUUID();
            File tempDir = new File(sharedDir);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }

            String fileName = "TempCode";
            String fileExtension = getFileExtension(language);
            String filePath = tempDir.getAbsolutePath() + "/" + fileName + fileExtension;

            saveCodeToFile(filePath, code);
            System.out.println("Code saved to file: " + filePath);

            String dockerImage = getDockerImage(language);
            String command = getDockerCommand(language, fileName + fileExtension, tempDir.getAbsolutePath());

            int testCount = 0;
            for (TestCase gradingTestCase : gradingTestCases) {
                testCount++;
                System.out.println("Executing command: " + command);

                ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                if (gradingTestCase.getInput() != null) {
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                        writer.write(gradingTestCase.getInput());
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

                String actualInput = gradingTestCase.getInput();
                String actualOutput = output.toString().trim().replaceAll("\\s+", "");
                String expectedOutput = gradingTestCase.getExpectedOutput().trim().replaceAll("\\s+", "");

                Map<String, String> result = new HashMap<>();
                result.put("testNumber", "테스트 " + testCount);
                result.put("status", actualOutput.equals(expectedOutput) ? "통과" : "실패");
                result.put("input", actualInput);
                result.put("expectedOutput", gradingTestCase.getExpectedOutput());
                result.put("actualOutput", output.toString().trim());
                results.add(result);
            }
            deleteDirectory(tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public String runCode(String code, String language) {
        return runCodeWithInput(code, language, null);
    }

    @Override
    public String runCodeWithInput(String code, String language, List<String> inputs) {
        try {
            // 임시 디렉토리 생성
            String sharedDir = "/tmp/tempDir_" + UUID.randomUUID();
            File tempDir = new File(sharedDir);
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

    @Override
    public void saveCodeToFile(String filePath, String code) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(code);
            System.out.println("Code written: " + code);
        }
    }

    @Override
    public String getFileExtension(String language) {
        switch (language) {
            case "python": return ".py";
            case "java": return ".java";
            case "c": return ".c";
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    @Override
    public String getDockerImage(String language) {
        switch (language) {
            case "python": return "python:3.8-slim";
            case "java": return "openjdk:11-slim";
            case "c": return "gcc:latest";
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    @Override
    public String getDockerCommand(String language, String filePath, String hostPath) {
        String sharedDir = hostPath;  // 호스트와 컨테이너 간 공유된 디렉토리
        String workDir = hostPath;    // 컨테이너 내부에서 작업할 디렉토리
        String containerFilePath = filePath;  // 컨테이너 내부에서의 파일 경로
        // 호스트의 파일 저장 경로와 컨테이너의 경로가 일치하도록 설정

        switch (language) {
            case "python":
                return String.format(
                        "docker run --rm -i -v %s:%s -w %s python:3.8-slim python %s",
                        sharedDir, sharedDir, workDir, containerFilePath
                );
            case "java":
                return String.format(
                        "docker run --rm -i -v %s:%s -w %s openjdk:11-slim sh -c 'javac %s && java TempCode'",
                        sharedDir, sharedDir, workDir, containerFilePath
                );
            case "c":
                return String.format(
                        "docker run --rm -i -v %s:%s -w %s gcc:latest sh -c 'gcc -o TempCode %s && ./TempCode'",
                        sharedDir, sharedDir, workDir, containerFilePath
                );
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }


    @Override
    public void deleteDirectory(File directory) {
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
