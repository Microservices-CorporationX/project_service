package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VacancyDto(
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        TeamRole position,

        @NotNull
        @Min(0)
        Long projectId,

        List<Long> candidatesId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        @NotNull
        @Min(0)
        Long createdBy,

        VacancyStatus status,

        Double salary,

        @Enumerated(EnumType.STRING)
        @NotNull
        WorkSchedule workSchedule,

        @Min(1)
        @NotNull
        Integer count,

        @ElementCollection
        List<Long> requiredSkillIds
) {

}
