package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Filters for the vacancy search")
public class FilterVacancyDto {

    @Schema(description = "Vacancy title")
    private String title;

    @Schema(description = "Salary", minimum = "0.0")
    @Positive (message = "Salary should be positive number")
    private Double salary;

    @Schema(description = "Work schedule")
    private WorkSchedule workSchedule;
}
