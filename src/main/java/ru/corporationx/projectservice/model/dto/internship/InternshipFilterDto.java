package ru.corporationx.projectservice.model.dto.internship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.InternshipStatus;
import ru.corporationx.projectservice.model.entity.TeamRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipFilterDto {
    private InternshipStatus statusPattern;
    private TeamRole rolePattern;
}
