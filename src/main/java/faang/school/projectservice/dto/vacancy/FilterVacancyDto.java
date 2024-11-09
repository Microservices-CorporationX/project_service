package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterVacancyDto {

    private String title;

    @Positive (message = "Salary should be positive number")
    private Double salary;

    private WorkSchedule workSchedule;
}
