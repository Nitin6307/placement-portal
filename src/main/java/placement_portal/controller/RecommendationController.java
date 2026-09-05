package placement_portal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import placement_portal.dto.RecommendationResponse;
import placement_portal.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/me")
    public ResponseEntity<List<RecommendationResponse>> recommendMyJobs(
            Authentication authentication) {

        return ResponseEntity.ok(
                recommendationService.recommendJobsForUser(
                        authentication.getName()
                )
        );
    }
}