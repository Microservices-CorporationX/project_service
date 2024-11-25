package faang.school.projectservice.dto;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {

    @NotNull(message = "Id must not be null")
    @Min(value = 0, message = "Id must be non-negative")
    private Long id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must not exceed 128 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Project id cannot be null")
    @Min(value = 0, message = "ProjectId must be non-negative")
    private Long projectId;

    private List<Long> candidateIds;
    private String createdAt;

    private String updatedAt;

    @NotNull(message = "Creator id cannot be null")
    private Long createdBy;

    private Long updatedBy;

    private VacancyStatus status;

    @NotNull(message = "Salary cannot be null")
    private Double salary;

    @NotNull(message = "Work Schedule cannot be null")
    private WorkSchedule workSchedule;

    @NotNull(message = "Count cannot be null")
    private Integer count;

    @NotNull(message = "Skill ids cannot be null")
    private List<Long> requiredSkillIds;
}
