package com.example.newhisolve.Controller;
import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/index")
    public String showIndexPage(@RequestParam("assignmentId") Long assignmentId,
                                @RequestParam("language") String language,
                                Model model) {
        model.addAttribute("assignmentId", assignmentId);
        model.addAttribute("language", language);

        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        model.addAttribute("assignment", assignment);

        return "index";
    }
}
