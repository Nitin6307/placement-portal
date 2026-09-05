package placement_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import placement_portal.dto.RecommendationResponse;
import placement_portal.entity.Job;
import placement_portal.entity.Skill;
import placement_portal.entity.Student;
import placement_portal.repository.JobRepository;
import placement_portal.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public List<RecommendationResponse> recommendJobs(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Set<String> studentSkills = student.getSkills()
                .stream()
                .map(Skill::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return jobRepository.findAll()
                .stream()
                .filter(job ->
                        job.getMinCgpa() == null
                                || student.getCgpa() >= job.getMinCgpa())
                .filter(job ->
                        job.getMaxBacklogs() == null
                                || student.getBacklogs() <= job.getMaxBacklogs())
                .filter(job ->
                        isBranchAllowed(
                                student.getBranch(),
                                job.getAllowedBranches()
                        ))
                .filter(job ->
                        hasMatchingSkill(
                                studentSkills,
                                job.getRequiredSkills()
                        ))
                .map(job -> toRecommendation(
                        job,
                        studentSkills
                ))
                .sorted(
                        Comparator.comparingDouble(
                                RecommendationResponse::getMatchPercentage
                        ).reversed()
                )
                .toList();
    }

    private RecommendationResponse toRecommendation(
            Job job,
            Set<String> studentSkills) {

        return RecommendationResponse.builder()
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .companyName(job.getCompany().getName())
                .packageLpa(job.getPackageLpa())
                .location(job.getCompany().getLocation())
                .matchPercentage(
                        calculateMatchPercentage(
                                studentSkills,
                                job.getRequiredSkills()
                        )
                )
                .build();
    }

    private boolean isBranchAllowed(
            String studentBranch,
            String allowedBranches) {

        if (allowedBranches == null
                || allowedBranches.isBlank()) {
            return true;
        }

        return Arrays.stream(allowedBranches.split(","))
                .map(String::trim)
                .anyMatch(branch ->
                        branch.equalsIgnoreCase(studentBranch));
    }

    private boolean hasMatchingSkill(
            Set<String> studentSkills,
            String requiredSkills) {

        if (requiredSkills == null
                || requiredSkills.isBlank()) {
            return true;
        }

        return Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .anyMatch(skill ->
                        studentSkills.contains(
                                skill.toLowerCase()
                        ));
    }

    private double calculateMatchPercentage(
            Set<String> studentSkills,
            String requiredSkills) {

        if (requiredSkills == null
                || requiredSkills.isBlank()) {
            return 100.0;
        }

        List<String> required = Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .toList();

        if (required.isEmpty()) {
            return 100.0;
        }

        long matched = required.stream()
                .filter(skill ->
                        studentSkills.contains(
                                skill.toLowerCase()
                        ))
                .count();

        return (matched * 100.0) / required.size();
    }
    public String getStudentUsername(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        if (student.getUser() == null) {
            throw new RuntimeException(
                    "Student is not linked to a user"
            );
        }

        return student.getUser().getUsername();
    }
    public List<RecommendationResponse> recommendJobsForUser(
            String username) {

        Student student = studentRepository
                .findByUserUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Student profile not found"));

        return recommendJobs(student.getId());
    }
}