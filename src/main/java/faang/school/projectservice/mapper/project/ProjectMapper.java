package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Qualifier
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "moments", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "childrenToIds")
    @Mapping(source = "stages", target = "stagesIds", qualifiedByName = "stagesToIds")
    @Mapping(source = "teams", target = "teamsIds", qualifiedByName = "teamsToIds")
    ProjectDto toDto(Project project);

    @Named("childrenToIds")
    default List<Long> toChildrenIds(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(Project::getId).toList();
    }

    @Named("stagesToIds")
    default List<Long> toStagesIds(List<Stage> stages) {
        if (stages == null) {
            return null;
        }
        return stages.stream().map(Stage::getStageId).toList();
    }

    @Named("teamsToIds")
    default List<Long> toTeamsIds(List<Team> teams) {
        if (teams == null) {
            return null;
        }
        return teams.stream().map(Team::getId).toList();
    }
}
