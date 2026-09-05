package placement_portal.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private Double minCgpa;

    @NotNull
    @Min(0)
    private Integer maxBacklogs;

    @NotBlank
    private String allowedBranches;

    @NotBlank
    private String requiredSkills;

    @NotBlank
    private String jobRole;

    @NotNull
    @DecimalMin("0.0")
    private Double packageLpa;

    private String location;

    private String website;
}