package placement_portal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import placement_portal.entity.Application;
import placement_portal.service.ApplicationService;
import jakarta.validation.Valid;
import placement_portal.dto.UpdateApplicationStatusRequest;


import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // Student applies for a job
    @PostMapping("/job/{jobId}")
    public ResponseEntity<Application> createApplication(
            @PathVariable Long jobId,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.createApplication(jobId, authentication)
        );
    }

    // Student can view their applications
    // Officer can view all applications
    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications(
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getAllApplications(authentication)
        );
    }

    // Get a specific application
    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(id, authentication)
        );
    }

    // Get applications of a specific student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Application>> getByStudent(
            @PathVariable Long studentId,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByStudent(
                        studentId,
                        authentication
                )
        );
    }

    // Get applications for a specific job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getByJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(
                        jobId,
                        authentication
                )
        );
    }

    // Officer updates application status
    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.updateStatus(
                        id,
                        request.getStatus(),
                        authentication
                )
        );
    }

    // Student can delete their own application
    // Officer can delete any application
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        applicationService.deleteApplication(id, authentication);

        return ResponseEntity.ok(
                "Application deleted successfully"
        );
    }
}