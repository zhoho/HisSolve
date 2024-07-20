package com.example.newhisolve.Controller;
import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Service.AssignmentService;
import com.example.newhisolve.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String showIndexPage(@RequestParam("assignmentId") Long assignmentId,
                                @RequestParam("language") String language,
                                Model model) {
        User student = userService.getCurrentUser();
        model.addAttribute("assignmentId", assignmentId);
        model.addAttribute("language", language);
        model.addAttribute("student", student);

        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        model.addAttribute("assignment", assignment);

        return "index";
    }
}
