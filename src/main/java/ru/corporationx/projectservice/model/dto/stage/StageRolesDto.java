package ru.corporationx.projectservice.model.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.TeamRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageRolesDto {
    private Long id;
    private TeamRole teamRole;
    private Integer count;
}
