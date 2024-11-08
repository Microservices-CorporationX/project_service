package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Builder
@Validated
public class NewVacancyDto {

    @NotBlank(message = "Vacancy title cannot be empty")
    private String name;

    @NotBlank(message = "Vacancy description cannot be empty")
    @Size(min = 10, max = 600, message = "Vacancy description must be at least 10 and under 600 characters long")
    private String description;

    @NotNull(message = "Project id must be a positive integer")
    @Positive(message = "Project id must be a positive integer")
    private long projectId;

    @NotNull(message = "Created by id cannot be empty")
    @Positive(message = "Created by id must be a positive integer")
    private Long createdBy;

    @NotNull(message = "Slavery is prohibited")
    @Positive(message = "Salary must be a positive number")
    private Double salary;

    @NotNull(message = "Work schedule cannot be empty")
    private WorkSchedule workSchedule;

    @NotNull(message = "Number of vacancies cannot be empty")
    @Positive(message = "Number of vacancies must be a positive integer")
    private Integer count;

    @NotNull(message = "Required skills ids list cannot be empty")
    @Size(min = 1, message = "Required skills ids list cannot be empty")
    private List<Long> requiredSkillIds;
}
