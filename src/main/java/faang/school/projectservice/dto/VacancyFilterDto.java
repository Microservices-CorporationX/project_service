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

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "ProjectId must not be null")
    @Min(value = 0, message = "ProjectId must be non-negative")
    private Long projectId;
}
