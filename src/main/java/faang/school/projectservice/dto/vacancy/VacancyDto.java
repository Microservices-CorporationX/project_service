package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VacancyDto {

    private Long id;

    @NotBlank(message = "Название вакансии не может быть пустым")
    private String name;

    @NotNull(message = "ID проекта не может быть null")
    private Long idProject;

    @NotBlank(message = "Описание вакансии не может быть пустым")
    private String description;

    @NotNull(message = "ID создателя не может быть null")
    private Long createdBy;

    @NotNull(message = "Значение не может быть null")
    private Integer count;

    @NotNull(message = "Значение не может быть null")
    private VacancyStatus status;
}