package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {

    @NotNull(message = "stageId не может быть null")
    @Min(value = 1, message = "stageId должен быть больше 0")
    private Long stageId;

    @NotNull(message = "Название этапа не может быть null")
    private String stageName;

    @NotNull(message = "Проект не может быть null")
    private Project project;

    @NotEmpty(message = "Список ролей не может быть пустым")
    private List<StageRolesDto> stageRolesDto;

}
