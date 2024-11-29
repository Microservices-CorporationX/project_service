package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageRolesDto {

    private Long stageRolesId;

    @NotNull(message = "teamRole не может быть null")
    private TeamRole teamRole;

    @Min(value = 1, message = "Количество человек должно быть больше 0")
    private Integer count;
}
