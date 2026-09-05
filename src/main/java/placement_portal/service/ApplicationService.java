package placement_portal.service;

import placement_portal.entity.Application;
import placement_portal.entity.Job;
import placement_portal.entity.Student;
import placement_portal.repository.ApplicationRepository;
import placement_portal.repository.JobRepository;
import placement_portal.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    // STUDENT: create own application
    @Transactional
    public Application createApplication(
            Long jobId,
            Authentication authentication) {

        Student student = studentRepository
                .findByUserUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Student profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        if (applicationRepository
                .existsByStudentIdAndJobId(student.getId(), jobId)) {

            throw new RuntimeException(
                    "Student has already applied for this job");
        }

        Application application = Application.builder()
                .student(student)
                .job(job)
                .status("APPLIED")
                .appliedDate(LocalDate.now().toString())
                .build();

        return applicationRepository.save(application);
    }

    // OFFICER: all applications
    // STUDENT: only own applications
    @Transactional(readOnly = true)
    public List<Application> getAllApplications(
            Authentication authentication) {

        if (isOfficer(authentication)) {
            return applicationRepository.findAll();
        }

        Student student = getCurrentStudent(authentication);

        return applicationRepository.findByStudentId(student.getId());
    }

    // OFFICER: any application
    // STUDENT: only own application
    @Transactional(readOnly = true)
    public Application getApplicationById(
            Long id,
            Authentication authentication) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));

        checkOwnership(application, authentication);

        return application;
    }

    // OFFICER: any student's applications
    // STUDENT: only own applications
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByStudent(
            Long studentId,
            Authentication authentication) {

        if (!isOfficer(authentication)) {

            Student student = getCurrentStudent(authentication);

            if (!student.getId().equals(studentId)) {
                throw new RuntimeException(
                        "You can only view your own applications");
            }
        }

        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found");
        }

        return applicationRepository.findByStudentId(studentId);
    }

    // OFFICER: applications for any job
    // STUDENT: only their own application for that job
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByJob(
            Long jobId,
            Authentication authentication) {

        if (!jobRepository.existsById(jobId)) {
            throw new RuntimeException("Job not found");
        }

        if (isOfficer(authentication)) {
            return applicationRepository.findByJobId(jobId);
        }

        Student student = getCurrentStudent(authentication);

        return applicationRepository
                .findByStudentIdAndJobId(student.getId(), jobId);
    }

    // ONLY OFFICER
    @Transactional
    public Application updateStatus(
            Long id,
            String status,
            Authentication authentication) {

        if (status == null || status.isBlank()) {
            throw new RuntimeException(
                    "Application status must not be blank");
        }

        if (!isOfficer(authentication)) {
            throw new RuntimeException(
                    "Only officers can update application status");
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!List.of(
                "APPLIED",
                "SHORTLISTED",
                "SELECTED",
                "REJECTED"
        ).contains(normalizedStatus)) {

            throw new RuntimeException(
                    "Invalid application status"
            );
        }

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        application.setStatus(normalizedStatus);

        return applicationRepository.save(application);
    }

    // STUDENT: own application
    // OFFICER: any application
    @Transactional
    public void deleteApplication(
            Long id,
            Authentication authentication) {

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        checkOwnership(application, authentication);

        applicationRepository.delete(application);
    }

    private Student getCurrentStudent(
            Authentication authentication) {

        return studentRepository
                .findByUserUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student profile not found"));
    }

    private void checkOwnership(
            Application application,
            Authentication authentication) {

        if (isOfficer(authentication)) {
            return;
        }

        Student currentStudent = getCurrentStudent(authentication);

        if (!currentStudent.getId()
                .equals(application.getStudent().getId())) {

            throw new RuntimeException(
                    "You can only access your own application");
        }
    }

    private boolean isOfficer(
            Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_OFFICER"));
    }
}