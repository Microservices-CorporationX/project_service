package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterVacancyDto {

    @NotBlank
    private String title;

    @NotNull
    @Positive
    private Double salary;

    @NotNull
    private WorkSchedule workSchedule;
}
