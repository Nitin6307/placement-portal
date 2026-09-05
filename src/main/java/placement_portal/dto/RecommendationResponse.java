package placement_portal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {

    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Double packageLpa;
    private String location;

    private Double matchPercentage;
}