package placement_portal.service;

import placement_portal.entity.Company;
import placement_portal.entity.Skill;
import placement_portal.entity.Student;
import placement_portal.repository.CompanyRepository;
import placement_portal.repository.StudentRepository;
import org.springframework.stereotype.Service;
import placement_portal.repository.JobRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EligibilityService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    public EligibilityService(
            StudentRepository studentRepository,
            CompanyRepository companyRepository,
            JobRepository jobRepository) {

        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
    }

    public boolean checkEligibility(Long studentId, Long companyId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        // CGPA check
        if (student.getCgpa() < company.getMinCgpa()) {
            return false;
        }

        // Backlog check
        if (student.getBacklogs() > company.getMaxBacklogs()) {
            return false;
        }

        // Branch check
        if (!isBranchAllowed(
                student.getBranch(),
                company.getAllowedBranches())) {
            return false;
        }

        // Skills check
        if (!hasRequiredSkills(
                student,
                company.getRequiredSkills())) {
            return false;
        }

        return true;
    }

    public List<Company> getEligibleCompanies(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        List<Company> companies = companyRepository.findAll();
        List<Company> eligibleCompanies = new ArrayList<>();

        for (Company company : companies) {

            if (student.getCgpa() < company.getMinCgpa()) {
                continue;
            }

            if (student.getBacklogs() > company.getMaxBacklogs()) {
                continue;
            }

            if (!isBranchAllowed(
                    student.getBranch(),
                    company.getAllowedBranches())) {
                continue;
            }

            if (!hasRequiredSkills(
                    student,
                    company.getRequiredSkills())) {
                continue;
            }

            eligibleCompanies.add(company);
        }

        return eligibleCompanies;
    }

    private boolean isBranchAllowed(
            String studentBranch,
            String allowedBranches) {

        if (allowedBranches == null ||
                allowedBranches.trim().isEmpty()) {
            return false;
        }

        return Arrays.stream(allowedBranches.split(","))
                .map(String::trim)
                .anyMatch(branch ->
                        branch.equalsIgnoreCase(studentBranch));
    }

    private boolean hasRequiredSkills(
            Student student,
            String requiredSkills) {

        if (requiredSkills == null ||
                requiredSkills.trim().isEmpty()) {
            return true;
        }

        List<String> studentSkills = student.getSkills()
                .stream()
                .map(Skill::getName)
                .map(String::trim)
                .toList();

        return Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .allMatch(requiredSkill ->
                        studentSkills.stream()
                                .anyMatch(studentSkill ->
                                        studentSkill.equalsIgnoreCase(
                                                requiredSkill)));
    }
    public boolean checkJobEligibility(Long studentId, Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        var job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // CGPA
        if (student.getCgpa() < job.getMinCgpa()) {
            return false;
        }

        // Backlogs
        if (student.getBacklogs() > job.getMaxBacklogs()) {
            return false;
        }

        // Branch
        if (!isBranchAllowed(
                student.getBranch(),
                job.getAllowedBranches())) {
            return false;
        }


        return true;
    }
}