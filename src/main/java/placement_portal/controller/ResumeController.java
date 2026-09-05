package placement_portal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import placement_portal.entity.Student;
import placement_portal.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final StudentRepository studentRepository;

    @Value("${server.port:8080}")
    private String serverPort;

    public ResumeController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping(
            value = "/me/resume",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            org.springframework.security.core.Authentication authentication
    ) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a PDF file"));
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                !originalFileName.toLowerCase().endsWith(".pdf")) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only PDF files are allowed"));
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "PDF size must be less than 5 MB"));
        }

        try {
            String username = authentication.getName();

            Student student = studentRepository
                    .findByUserUsername(username)
                    .orElseThrow(() ->
                            new RuntimeException("Student profile not found"));

            Path uploadDir = Paths.get("uploads/resumes");
            Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + ".pdf";
            Path filePath = uploadDir.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String resumeUrl =
                    "http://localhost:" + serverPort +
                            "/api/students/resume/" + fileName;

            student.setResumeUrl(resumeUrl);
            studentRepository.save(student);

            return ResponseEntity.ok(Map.of(
                    "message", "Resume uploaded successfully",
                    "resumeUrl", resumeUrl
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload resume"));
        }
    }

    @GetMapping("/resume/{fileName}")
    public ResponseEntity<byte[]> viewResume(
            @PathVariable String fileName
    ) throws IOException {

        Path filePath = Paths.get("uploads/resumes").resolve(fileName)
                .normalize();

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] file = Files.readAllBytes(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}