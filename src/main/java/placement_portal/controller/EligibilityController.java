package placement_portal.controller;

import placement_portal.entity.Company;
import placement_portal.service.EligibilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(
            EligibilityService eligibilityService) {

        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/student/{studentId}/company/{companyId}")
    public ResponseEntity<Boolean> checkEligibility(
            @PathVariable Long studentId,
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                eligibilityService.checkEligibility(
                        studentId,
                        companyId
                )
        );
    }

    @GetMapping("/student/{studentId}/companies")
    public ResponseEntity<List<Company>> getEligibleCompanies(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                eligibilityService
                        .getEligibleCompanies(studentId)
        );
    }
}