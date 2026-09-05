package placement_portal.service;

import org.springframework.stereotype.Service;
import placement_portal.dto.SkillGapResponse;
import placement_portal.entity.Job;
import placement_portal.entity.Skill;
import placement_portal.entity.Student;
import placement_portal.repository.JobRepository;
import placement_portal.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SkillGapService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public SkillGapService(
            StudentRepository studentRepository,
            JobRepository jobRepository) {

        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    public SkillGapResponse analyzeSkillGap(
            Long studentId,
            Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        List<String> requiredSkills =
                parseSkills(job.getRequiredSkills());

        List<String> studentSkills =
                student.getSkills()
                        .stream()
                        .map(Skill::getName)
                        .toList();

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {

            boolean matched = false;

            for (String studentSkill : studentSkills) {

                if (studentSkill.equalsIgnoreCase(requiredSkill)) {
                    matched = true;
                    break;
                }
            }

            if (matched) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        int matchPercentage = 0;

        if (!requiredSkills.isEmpty()) {
            matchPercentage =
                    (matchedSkills.size() * 100)
                            / requiredSkills.size();
        }

        return SkillGapResponse.builder()
                .studentId(student.getId())
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .requiredSkills(requiredSkills)
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .matchPercentage(matchPercentage)
                .build();
    }

    private List<String> parseSkills(String skills) {

        if (skills == null || skills.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .toList();
    }
}