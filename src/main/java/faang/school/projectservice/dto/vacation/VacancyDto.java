package faang.school.projectservice.dto.vacation;

import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record VacancyDto(
    @Min(value = 1, message = "Value <vacancy id> has to be min 1")
    Long id,
    @Size(min = 1, max = 254, message = "Size <name> has to be from 1 to 254 characters long")
    String name,
    @Size(min = 1, max = 65534, message = "Size <description> has to be from 1 to 65534 characters long")
    String description,
    @Min(value = 1, message = "Value <projectId> has to be min 1")
    Long projectId,
    List<Long> candidates,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    @Min(value = 1, message = "Value <createdBy> has to be min 1")
    Long createdBy,
    @Min(value = 1, message = "Value <updatedBy> has to be min 1")
    Long updatedBy,
    @Size(min = 1, max = 49, message = "Size <status> has to be from 1 to 49 characters long")
    VacancyStatus status,
    @Min(value = 0, message = "Value <salary> has to be min 0")
    Double salary,
    WorkSchedule workSchedule,
    @Min(value = 1, message = "Value <count> has to be min 1")
    Integer count,
    List<Long> requiredSkillIds){
}