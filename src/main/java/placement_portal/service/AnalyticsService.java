package placement_portal.service;

import org.springframework.stereotype.Service;
import placement_portal.entity.Application;
import placement_portal.repository.ApplicationRepository;
import placement_portal.repository.CompanyRepository;
import placement_portal.repository.JobRepository;
import placement_portal.repository.StudentRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    public AnalyticsService(
            ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            CompanyRepository companyRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
    }

    public Map<String, Long> getApplicationStatistics() {

        List<Application> applications =
                applicationRepository.findAll();

        Map<String, Long> statistics = new LinkedHashMap<>();

        statistics.put("totalApplications",
                (long) applications.size());

        statistics.put("applied",
                applications.stream()
                        .filter(a -> "APPLIED".equalsIgnoreCase(a.getStatus()))
                        .count());

        statistics.put("shortlisted",
                applications.stream()
                        .filter(a -> "SHORTLISTED".equalsIgnoreCase(a.getStatus()))
                        .count());

        statistics.put("selected",
                applications.stream()
                        .filter(a -> "SELECTED".equalsIgnoreCase(a.getStatus()))
                        .count());

        statistics.put("rejected",
                applications.stream()
                        .filter(a -> "REJECTED".equalsIgnoreCase(a.getStatus()))
                        .count());

        return statistics;
    }

    public Map<String, Object> getPlacementStatistics() {

        long totalStudents = studentRepository.count();
        long totalCompanies = companyRepository.count();
        long totalJobs = jobRepository.count();
        long totalApplications = applicationRepository.count();

        long selectedStudents = applicationRepository.findAll()
                .stream()
                .filter(a -> "SELECTED".equalsIgnoreCase(a.getStatus()))
                .map(a -> a.getStudent().getId())
                .distinct()
                .count();

        double placementPercentage = 0;

        if (totalStudents > 0) {
            placementPercentage =
                    (selectedStudents * 100.0) / totalStudents;
        }

        Map<String, Object> statistics = new LinkedHashMap<>();

        statistics.put("totalStudents", totalStudents);
        statistics.put("totalCompanies", totalCompanies);
        statistics.put("totalJobs", totalJobs);
        statistics.put("totalApplications", totalApplications);
        statistics.put("selectedStudents", selectedStudents);
        statistics.put("placementPercentage", placementPercentage);

        return statistics;
    }
}