package placement_portal.service;

import org.springframework.transaction.annotation.Transactional;
import placement_portal.entity.Company;
import placement_portal.entity.Job;
import placement_portal.repository.CompanyRepository;
import placement_portal.repository.JobRepository;
import org.springframework.stereotype.Service;
import placement_portal.repository.ApplicationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;

    public JobService(JobRepository jobRepository,
                      CompanyRepository companyRepository,
                      ApplicationRepository applicationRepository) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
    }

    public Job createJob(Long companyId, Job job) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        job.setCompany(company);
        job.setLocation(company.getLocation());

        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> getJobsByCompany(Long companyId) {

        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company not found");
        }

        return jobRepository.findByCompanyId(companyId);
    }

    public Job updateJob(Long id, Job job) {

        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        existingJob.setTitle(job.getTitle());
        existingJob.setDescription(job.getDescription());
        existingJob.setPackageLpa(job.getPackageLpa());
        existingJob.setRequiredSkills(job.getRequiredSkills());
        existingJob.setMinCgpa(job.getMinCgpa());
        existingJob.setMaxBacklogs(job.getMaxBacklogs());
        existingJob.setAllowedBranches(job.getAllowedBranches());
        existingJob.setLocation(existingJob.getCompany().getLocation());

        return jobRepository.save(existingJob);
    }

    @Transactional
    public void deleteJob(Long id) {

        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found");
        }

        applicationRepository.deleteByJobId(id);
        jobRepository.deleteById(id);
    }
}