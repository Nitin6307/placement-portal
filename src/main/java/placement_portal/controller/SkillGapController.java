package placement_portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import placement_portal.dto.SkillGapResponse;
import placement_portal.service.SkillGapService;

@RestController
@RequestMapping("/api/skill-gap")
public class SkillGapController {

    private final SkillGapService skillGapService;

    public SkillGapController(SkillGapService skillGapService) {
        this.skillGapService = skillGapService;
    }

    @GetMapping("/student/{studentId}/job/{jobId}")
    public ResponseEntity<SkillGapResponse> analyzeSkillGap(
            @PathVariable Long studentId,
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                skillGapService.analyzeSkillGap(
                        studentId,
                        jobId
                )
        );
    }
}