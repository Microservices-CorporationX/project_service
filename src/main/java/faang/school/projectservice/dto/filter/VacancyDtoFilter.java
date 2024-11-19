package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VacancyDtoFilter {
    @NotBlank(message = "Название вакансии не может быть пустым")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Значение не может быть null")
    private VacancyStatus status;
}