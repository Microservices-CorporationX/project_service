package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class VacancyDto {
    private long id;
    private TeamRole position;
    private Integer count;
    private long creatorId;
}
