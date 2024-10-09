package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.ContestService;
import com.example.newhisolve.Service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;
    private final SubmissionService submissionService;

    @GetMapping("/contest/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateContestForm(Model model) {
        model.addAttribute("contest", new Contest());
        return "create_contest";
    }

    @PostMapping("/contest/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createContest(@ModelAttribute Contest contest, Principal principal) {
        contestService.createContest(contest, principal.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/contest/join")
    public String showJoinContestForm() {
        return "join_contest";
    }

    @PostMapping("/contest/join")
    public String joinContest(@RequestParam String code, Principal principal) {
        contestService.joinContest(code, principal.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/contest/{id}")
    public String viewContest(@PathVariable Long id, Model model) {
        Contest contestEntity = contestService.findById(id);
        model.addAttribute("contest", contestEntity);
        model.addAttribute("problems", contestService.findProblemsByContest(contestEntity));
        return "contest_detail";
    }

    @GetMapping("/admin_contest/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewAdminContest(@PathVariable Long id, Model model) {
        Contest contestEntity = contestService.findById(id);
        model.addAttribute("contest", contestEntity);
        model.addAttribute("users", contestEntity.getUsers());
        model.addAttribute("problems", contestService.findProblemsByContest(contestEntity));

        // 문제별 참여자 수 계산
        Map<Long, Long> problemParticipantCounts = contestService.getParticipantCountForProblems(id);
        model.addAttribute("problemParticipantCounts", problemParticipantCounts); // 모델에 추가


        return "admin_contest_detail";
    }

    @PostMapping("/contest/removeUser")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeUser(@RequestParam Long contestId, @RequestParam Long userId) {
        contestService.removeUserFromContest(contestId, userId);
        submissionService.deleteSubmissionsByUserId(userId);
        submissionService.deleteSavedCodeByUserId(userId);

        return "redirect:/admin_contest/" + contestId;
    }

    @GetMapping("/contest/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editContest(@RequestParam Long contestId, Model model) {
        Contest contestEntity = contestService.findById(contestId);
        model.addAttribute("contest", contestEntity);
        return "edit_contest";
    }

    @PostMapping("/contest/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateContest(@ModelAttribute Contest contest) {
        contestService.updateContest(contest);
        return "redirect:/admin_contest/" + contest.getId();
    }

    @PostMapping("/contest/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteContest(@RequestParam Long contestId) {
        Contest contest = contestService.findById(contestId);
        contestService.deleteContest(contest);
        return "redirect:/dashboard";
    }

    @GetMapping("/admin_contest/rankDashboard/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewRankDashboard(@PathVariable Long id, Model model) {
        Contest contestEntity = contestService.findById(id);
        model.addAttribute("contest", contestEntity);

        List<User> sortedUsers = contestService.getSortedUsersByTotalScore(id);
        model.addAttribute("users", sortedUsers);

        // 유저별 총 점수 계산
        Map<Long, Integer> userTotalScores = new HashMap<>();
        for (User user : sortedUsers) {
            int totalScore = contestService.getTotalScoreByUserAndContest(user.getId(), contestEntity.getId());
            userTotalScores.put(user.getId(), totalScore);
        }
        model.addAttribute("userTotalScores", userTotalScores);

        // 유저별 문제별 점수 리스트 계산
        Map<Long, List<Integer>> userProblemScores = contestService.getUserProblemScores(id);
        model.addAttribute("userProblemScores", userProblemScores);

        return "rank_dashboard";
    }

    @GetMapping("/contest/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportContestToExcel(@RequestParam Long contestId) {
        Contest contest = contestService.findById(contestId);
        List<User> users = contest.getUsers();

        // 유저별 문제 점수 데이터를 가져옴
        Map<Long, List<Integer>> userProblemScores = contestService.getUserProblemScores(contestId);
        Map<Long, List<Integer>> testCasesPassed = contestService.getUserTestCasesPassed(contestId);
        Map<Long, List<Boolean>> problemSolvedStatus = contestService.getUserProblemSolvedStatus(contestId);
        Map<Long, List<String>> submissionTimes = contestService.getUserSubmissionTimes(contestId);

        // 문제의 최대 개수를 동적으로 계산
        int maxProblems = userProblemScores.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Contest Name at the top of the sheet
            Row contestNameRow = sheet.createRow(0);
            contestNameRow.createCell(0).setCellValue("Contest Name: " + contest.getName());

            // 대회 기간 추가
            Row contestDurationRow = sheet.createRow(1);
            contestDurationRow.createCell(0).setCellValue("Contest Duration: "
                    + contest.getStartDate().toLocalDate() + " ~ " + contest.getDueDate().toLocalDate());

            // 엑셀 다운로드 시간 추가
            Row downloadTimeRow = sheet.createRow(2);
            downloadTimeRow.createCell(0).setCellValue("Downloaded At: " + LocalDate.now());

            // Header
            Row headerRow = sheet.createRow(3); // 헤더는 3번째 줄부터 시작
            headerRow.createCell(0).setCellValue("Serial No.");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");

            // 문제 세부 정보 헤더 추가 (예: 문제 1, 문제 2, ... 문제 N)
            for (int i = 1; i <= maxProblems; i++) {
                headerRow.createCell(2 + i).setCellValue("문제 " + i);
                headerRow.createCell(2 + maxProblems + i).setCellValue("통과한 테스트케이스 수 (문제 " + i + ")");
                headerRow.createCell(2 + maxProblems * 2 + i).setCellValue("문제 해결 여부 (문제 " + i + ")");
                headerRow.createCell(2 + maxProblems * 3 + i).setCellValue("제출 시간 (문제 " + i + ")");
            }

            headerRow.createCell(3 + maxProblems * 4).setCellValue("Total Score");

            // Body
            int rowNum = 4; // 데이터는 4번째 줄부터 시작
            for (User user : users) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum - 3); // 일련번호
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());

                // 유저의 각 문제 점수 가져오기
                List<Integer> scores = userProblemScores.get(user.getId());
                List<Integer> testCases = testCasesPassed.get(user.getId());
                List<Boolean> solved = problemSolvedStatus.get(user.getId());
                List<String> submissionTime = submissionTimes.get(user.getId());

                if (scores != null) {
                    for (int i = 0; i < scores.size(); i++) {
                        row.createCell(3 + i).setCellValue(scores.get(i)); // 점수 저장
                        row.createCell(3 + maxProblems + i).setCellValue(testCases.get(i)); // 통과한 테스트케이스 수
                        row.createCell(3 + maxProblems * 2 + i).setCellValue(solved.get(i) ? "해결" : "미해결"); // 문제 해결 여부
                        row.createCell(3 + maxProblems * 3 + i).setCellValue(submissionTime.get(i)); // 제출 시간
                    }
                }

                int totalScore = contestService.getTotalScoreByUserAndContest(user.getId(), contestId);
                row.createCell(3 + maxProblems * 4).setCellValue(totalScore);

                rowNum++;
            }

            // Write the output to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            // 파일 이름에 contest.name과 오늘 날짜를 포함
            String fileName = URLEncoder.encode(contest.getName() + "_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            fileName = fileName.replaceAll("\\+", "%20"); // 공백을 %20으로 변경

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel file", e);
        }
    }



    @GetMapping("/contest/search")
    @ResponseBody
    public List<Map<String, Object>> searchContests(@RequestParam("searchQuery") String searchQuery) {
        List<Contest> contests = contestService.searchContestsByName(searchQuery);

        // 각 Contest 정보를 맵에 담아 응답에 포함
        List<Map<String, Object>> contestResponse = new ArrayList<>();
        for (Contest contest : contests) {
            Map<String, Object> contestData = new HashMap<>();
            contestData.put("id", contest.getId());
            contestData.put("name", contest.getName());
            contestData.put("description", contest.getDescription());
            contestData.put("status", contest.getStatus());
            contestData.put("participantCount", contest.getParticipantCount()); // 참여자 수 추가
            contestData.put("duration", contest.getDuration()); // 기간 추가
            contestResponse.add(contestData);
        }

        return contestResponse; // 수정된 대회 리스트 반환
    }


    @GetMapping("/contest/autocomplete")
    @ResponseBody
    public List<Contest> autocompleteContests(@RequestParam("searchQuery") String searchQuery) {
        return contestService.searchContestsByName(searchQuery);
    }



}
