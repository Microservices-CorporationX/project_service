package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class VacancyDto {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @Min(1L)
    private Long projectId;

    @NotNull
    List<Long> candidateIds;

    private LocalDateTime createdAt;

    @Min(1)
    private Double salary;

    @NotNull
    WorkSchedule workSchedule;

    @NotNull
    List<Long> requiredSkillIds;

    @Min(1)
    int count;
}