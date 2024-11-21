package faang.school.projectservice.dto;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    @NotBlank(message = "Name cannot be null")
    private String name;
    @NotBlank(message = "Description cannot be null")
    private String description;
    @NotNull(message = "Project id cannot be null")
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
