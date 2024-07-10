package com.example.newhisolve.Service;

import com.example.newhisolve.Model.Assignment;
import com.example.newhisolve.Model.Course;
import com.example.newhisolve.Model.Submission;
import com.example.newhisolve.Model.User;
import com.example.newhisolve.Repository.AssignmentRepository;
import com.example.newhisolve.Repository.CourseRepository;
import com.example.newhisolve.Repository.SubmissionRepository;
import com.example.newhisolve.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Assignment createAssignment(Assignment assignment, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    @Override
    public Assignment findById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    @Override
    public List<Submission> findSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    @Override
    public void submitAssignment(Long assignmentId, String code, String language, String username) {
        User student = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setCode(code);
        submission.setLanguage(language);
        submission.setResult("Pending");
        submissionRepository.save(submission);

        // 채점 로직 추가 (외부 API 호출 등)
        // 예시로 간단한 채점 로직을 추가합니다.
        String result = gradeCode(submission);
        submission.setResult(result);
        submissionRepository.save(submission);
    }

    private String gradeCode(Submission submission) {
        // 실제 채점 로직을 구현합니다.
        // 예를 들어, 외부 채점 API를 호출할 수 있습니다.
        // 여기서는 간단한 예시로 코드를 평가합니다.
        if (submission.getCode().contains("Hello")) {
            return "Success";
        } else {
            return "Fail";
        }
    }
}
