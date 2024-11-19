package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {


    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "teams", ignore = true)
    Project toProject(ProjectDto projectDto);


    @Mapping(source = "parentProject", target = "parentProjectId", qualifiedByName = "getParentProjectId")
    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "getChildrenIds")
    @Mapping(source = "teams", target = "teamsIds", qualifiedByName = "getTeamsIds")
    ProjectDto toProjectDto(Project project);

    void update(ProjectDto projectDto, @MappingTarget Project project);


    @Named("getParentProjectId")
    default Long getParentProjectId(Project parentProject) {
        if (parentProject == null) {
            return null;
        }
        return parentProject.getId();
    }

    @Named("getChildrenIds")
    default List<Long> getChildrenIds(List<Project> subProjects) {
        if (subProjects == null) {
            return new ArrayList<>();
        }
        return subProjects.stream().map(Project::getId).toList();
    }

    @Named("getTeamsIds")
    default List<Long> getTeamsIds(List<Team> teams) {
        if (teams == null) {
            return new ArrayList<>();
        }
        return teams.stream().map(Team::getId).toList();
    }
}
