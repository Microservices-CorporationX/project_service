package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacancyDto {
    private String name;
    private String description;
    private TeamRole position;
    private Project project;
    private VacancyStatus status;
    private Double salary;
    private Integer count;
}
