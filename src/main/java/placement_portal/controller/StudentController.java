package placement_portal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import placement_portal.entity.Skill;
import placement_portal.entity.Student;
import placement_portal.repository.SkillRepository;
import placement_portal.service.StudentService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SkillRepository skillRepository;

    // STUDENT: create own profile
    @PostMapping
    public ResponseEntity<Student> createStudent(
            @Valid @RequestBody Student student,
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.createStudent(student, authentication)
        );
    }

    // STUDENT: view own profile
    @GetMapping("/me")
    public ResponseEntity<Student> getMyProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getMyProfile(authentication)
        );
    }

    // STUDENT: update own profile
    @PutMapping("/me")
    public ResponseEntity<Student> updateMyProfile(
            @Valid @RequestBody Student student,
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.updateMyProfile(student, authentication)
        );
    }

    // STUDENT: delete own profile
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyProfile(
            Authentication authentication) {

        studentService.deleteMyProfile(authentication);
        return ResponseEntity.ok("Student profile deleted successfully");
    }

    // STUDENT: add skill
    @PostMapping("/me/skills/{skillId}")
    public ResponseEntity<Student> addSkill(
            @PathVariable Long skillId,
            Authentication authentication) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        return ResponseEntity.ok(
                studentService.addSkill(skillId, authentication, skill)
        );
    }

    // STUDENT: remove skill
    @DeleteMapping("/me/skills/{skillId}")
    public ResponseEntity<Student> removeSkill(
            @PathVariable Long skillId,
            Authentication authentication) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        return ResponseEntity.ok(
                studentService.removeSkill(skillId, authentication, skill)
        );
    }

    // OFFICER: view all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            Authentication authentication) {

        boolean officer = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"));

        if (!officer) {
            throw new RuntimeException("Only officers can view all students");
        }

        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // OFFICER: view student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id,
            Authentication authentication) {

        boolean officer = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"));

        if (!officer) {
            throw new RuntimeException("Only officers can view student details");
        }

        return ResponseEntity.ok(studentService.getStudentById(id));
    }
}