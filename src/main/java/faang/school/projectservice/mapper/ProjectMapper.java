package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "visibility", target = "visibility", qualifiedByName = "visibilityToString")
    @Mapping(source = "teams", target = "teamIds")
    ProjectDto toDto(Project project);

    @Mapping(target = "storageSize", ignore = true)
    @Mapping(target = "maxStorageSize", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    @Mapping(source = "visibility", target = "visibility", qualifiedByName = "stringToVisibility")
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

    @Named("statusToString")
    default String statusToString(ProjectStatus status) {
        return status != null ? status.name() : null;
    }
    @Named("stringToStatus")
    default ProjectStatus stringToStatus(String status) {
        return status != null ? ProjectStatus.valueOf(status) : null;
    }
    @Named("visibilityToString")
    default String visibilityToString(ProjectVisibility visibility) {
        return visibility != null ? visibility.name() : null;
    }
    @Named("stringToVisibility")
    default ProjectVisibility stringToVisibility(String visibility) {
        return visibility != null ? ProjectVisibility.valueOf(visibility) : null;
    }
}
