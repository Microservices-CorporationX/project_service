package faang.school.projectservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyFilterDto {

    private String name;

    @Min(value = 0, message = "ProjectId must be non-negative")
    private Long projectId;
}
