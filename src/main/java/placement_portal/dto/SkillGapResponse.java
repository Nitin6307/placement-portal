package placement_portal.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapResponse {

    private Long studentId;

    private Long jobId;

    private String jobTitle;

    private List<String> requiredSkills;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private int matchPercentage;
}