package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Builder
@Validated
public class VacancyDto {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Long projectId;

    List<Long> candidateIds;

    private Double salary;

    @NotNull
    WorkSchedule workSchedule;

    List<Long> requiredSkillIds;

    @NotNull
    private Long createdBy;

    private Long updatedBy;

    int count;
}