package placement_portal.service;

import placement_portal.entity.Company;
import placement_portal.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company updateCompany(Long id, Company company) {

        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        existingCompany.setName(company.getName());
        existingCompany.setDescription(company.getDescription());
        existingCompany.setMinCgpa(company.getMinCgpa());
        existingCompany.setMaxBacklogs(company.getMaxBacklogs());
        existingCompany.setAllowedBranches(company.getAllowedBranches());
        existingCompany.setRequiredSkills(company.getRequiredSkills());
        existingCompany.setJobRole(company.getJobRole());
        existingCompany.setPackageLpa(company.getPackageLpa());
        existingCompany.setLocation(company.getLocation());
        existingCompany.setWebsite(company.getWebsite());

        return companyRepository.save(existingCompany);
    }

    public void deleteCompany(Long id) {

        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found");
        }

        companyRepository.deleteById(id);
    }
}