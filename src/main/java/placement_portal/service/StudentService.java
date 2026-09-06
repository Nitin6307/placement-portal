package placement_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import placement_portal.entity.Skill;
import placement_portal.entity.Student;
import placement_portal.entity.User;
import placement_portal.repository.StudentRepository;
import placement_portal.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public Student createStudent(Student student, Authentication authentication) {

        User user = getCurrentUser(authentication);

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only students can create a student profile");
        }

        if (studentRepository.findByUserUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Student profile already exists");
        }

        student.setUser(user);

        return studentRepository.save(student);
    }

    public Student getMyProfile(Authentication authentication) {
        return studentRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Student profile not found"));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));
    }

    public Student updateMyProfile(
            Student updatedStudent,
            Authentication authentication) {

        Student student = getMyProfile(authentication);

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setBranch(updatedStudent.getBranch());
        student.setCgpa(updatedStudent.getCgpa());
        student.setBacklogs(updatedStudent.getBacklogs());
        student.setGraduationYear(updatedStudent.getGraduationYear());
        student.setPhone(updatedStudent.getPhone());
        student.setResumeUrl(updatedStudent.getResumeUrl());

        return studentRepository.save(student);
    }

    public void deleteMyProfile(Authentication authentication) {

        Student student = getMyProfile(authentication);

        studentRepository.delete(student);
    }

    public Student addSkill(
            Long skillId,
            Authentication authentication,
            Skill skill) {

        Student student = getMyProfile(authentication);

        if (!student.getSkills().contains(skill)) {
            student.getSkills().add(skill);
        }

        return studentRepository.save(student);
    }

    public Student removeSkill(
            Long skillId,
            Authentication authentication,
            Skill skill) {

        Student student = getMyProfile(authentication);

        student.getSkills().removeIf(
                existingSkill -> existingSkill.getId().equals(skillId)
        );

        return studentRepository.save(student);
    }

    private User getCurrentUser(Authentication authentication) {

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}