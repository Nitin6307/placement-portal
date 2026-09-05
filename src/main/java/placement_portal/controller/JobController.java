package placement_portal.controller;

import placement_portal.entity.Job;
import placement_portal.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/company/{companyId}")
    public ResponseEntity<Job> createJob(
            @PathVariable Long companyId,
           @Valid @RequestBody Job job) {

        return ResponseEntity.ok(
                jobService.createJob(companyId, job)
        );
    }

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {

        return ResponseEntity.ok(
                jobService.getAllJobs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Job>> getJobsByCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                jobService.getJobsByCompany(companyId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(
            @PathVariable Long id,
           @Valid @RequestBody Job job) {

        return ResponseEntity.ok(
                jobService.updateJob(id, job)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return ResponseEntity.noContent().build();
    }
}