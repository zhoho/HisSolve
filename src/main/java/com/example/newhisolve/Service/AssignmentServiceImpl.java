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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public Assignment createAssignment(Assignment assignment, Long classId) {
        Course courseEntity = courseRepository.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
        assignment.setCourseEntity(courseEntity);
        return assignmentRepository.save(assignment);
    }

    @Override
    public Assignment findById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    @Override
    public void submitAssignment(Long assignmentId, String code, String studentUsername) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        User student = userRepository.findByUsername(studentUsername).orElseThrow(() -> new RuntimeException("Student not found"));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setCode(code);
        submission.setSubmittedAt(LocalDateTime.now());

        // 여기에서 코드를 채점하는 로직을 추가할 수 있습니다.
        // 채점 결과를 submission.setResult(...)로 설정합니다.
        submission.setResult("Pending grading");

        submissionRepository.save(submission);
    }

    @Override
    public List<Submission> findSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }
}
