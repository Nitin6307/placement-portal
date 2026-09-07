package placement_portal.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private Double packageLpa;

    @NotBlank
    private String requiredSkills;

    @NotNull
    @DecimalMin("0.0")
    private Double minCgpa;

    @NotNull
    @Min(0)
    private Integer maxBacklogs;

    @NotBlank
    private String allowedBranches;


    private String location;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}