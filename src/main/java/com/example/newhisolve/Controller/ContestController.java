package com.example.newhisolve.Controller;

import com.example.newhisolve.Model.Contest;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.ContestService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

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
        return "admin_contest_detail";
    }

    @PostMapping("/contest/removeUser")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeUser(@RequestParam Long contestId, @RequestParam Long userId) {
        contestService.removeUserFromContest(contestId, userId);
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

        // 문제의 최대 개수를 동적으로 계산
        int maxProblems = userProblemScores.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Serial No.");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");

            // 문제 세부 정보 헤더 추가 (예: 문제 1, 문제 2, ... 문제 N)
            for (int i = 1; i <= maxProblems; i++) {
                headerRow.createCell(2 + i).setCellValue("문제 " + i);
            }

            headerRow.createCell(3 + maxProblems).setCellValue("Total Score");

            // Body
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum); // 일련번호
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());

                // 유저의 각 문제 점수 가져오기
                List<Integer> scores = userProblemScores.get(user.getId());
                if (scores != null) {
                    for (int i = 0; i < scores.size(); i++) {
                        row.createCell(3 + i).setCellValue(scores.get(i)); // 점수 저장
                    }
                }

                int totalScore = contestService.getTotalScoreByUserAndContest(user.getId(), contestId);
                row.createCell(3 + maxProblems).setCellValue(totalScore);

                rowNum++;
            }

            // Write the output to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            // 파일 이름에 contest.name과 오늘 날짜를 포함
            String fileName = URLEncoder.encode(contest.getName() + "_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8.name());
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
    public List<Contest> searchContests(@RequestParam("searchQuery") String searchQuery) {
        return contestService.searchContestsByName(searchQuery);
    }

    @GetMapping("/contest/autocomplete")
    @ResponseBody
    public List<Contest> autocompleteContests(@RequestParam("searchQuery") String searchQuery) {
        return contestService.searchContestsByName(searchQuery);
    }

}
