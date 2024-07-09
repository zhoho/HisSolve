//package com.example.newhisolve.Service;
//
//import com.example.newhisolve.Model.Assignment;
//import com.example.newhisolve.Model.User;
//import com.example.newhisolve.Repository.AssignmentRepository;
//import com.example.newhisolve.Repository.ClassRepository;
//import com.example.newhisolve.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//public interface ClassService {
//    Class createClass(Class classEntity, String professorUsername);
//    void joinClass(String code, String studentUsername);
//    Class findById(Long id);
//    List<Class> findByUser(User user);
//    List<Assignment> findAssignmentsByClass(Class classEntity);
//}
//
//@Service
//public class ClassServiceImpl implements ClassService {
//    @Autowired
//    private ClassRepository classRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public Class createClass(Class classEntity, String professorUsername) {
//        User professor = userRepository.findByUsername(professorUsername).orElseThrow(() -> new RuntimeException("Professor not found"));
//        classEntity.setProfessor(professor);
//        classEntity.setCode(UUID.randomUUID().toString());
//        return classRepository.save(classEntity);
//    }
//
//    @Override
//    public void joinClass(String code, String studentUsername) {
//        Class classEntity = classRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Class not found"));
//        User student = userRepository.findByUsername(studentUsername).orElseThrow(() -> new RuntimeException("Student not found"));
//        Class.getStudents().add(student);
//        classRepository.save(classEntity);
//    }
//
//    @Override
//    public Class findById(Long id) {
//        return classRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));
//    }
//
//    @Override
//    public List<Class> findByUser(User user) {
//        if (user.getRole().equals("PROFESSOR")) {
//            return classRepository.findByProfessor(user);
//        } else {
//            return classRepository.findByStudentsContaining(user);
//        }
//    }
//
//    @Override
//    public List<Assignment> findAssignmentsByClass(Class classEntity) {
//        return AssignmentRepository.findByClassEntity(classEntity);
//    }
//}
