package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "teams", target = "teamIds")
    ProjectDto toDto(Project project);

    @Mapping(target = "storageSize", ignore = true)
    @Mapping(target = "maxStorageSize", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(target = "moments", ignore = true)
    @Mapping(target = "meets", ignore = true)
    Project toEntity(ProjectDto projectDto);

    default List<Long> mapTeamsToTeamIds(List<Team> teams) {
        if(teams == null) {
            return new ArrayList<>();
        }

        return teams.stream()
                .map(Team::getId)
                .toList();
    }
}
