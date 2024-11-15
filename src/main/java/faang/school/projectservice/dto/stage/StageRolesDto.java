package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageRolesDto {
    @NotNull(message = "stageRolesId не может быть null")
    @Min(value = 1, message = "stageRolesId должен быть больше 0")
    private Long stageRolesId;

    @NotNull(message = "teamRole не может быть null")
    private TeamRole teamRole;

    @NotNull(message = "Количество человек не может быть null")
    @Min(value = 1, message = "Количество человек должно быть больше 0")
    private Integer count;
}
