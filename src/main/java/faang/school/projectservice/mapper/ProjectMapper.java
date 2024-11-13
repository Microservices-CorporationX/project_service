package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "teams", target = "teamIds")
    @Mapping(source = "owner.id", target = "owner")
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    void update(ProjectDto projectDto, @MappingTarget Project project);

    default List<Long> mapTeamsToTeamIds(List<Team> teams) {
        if(teams == null) {
            return new ArrayList<>();
        }

        return teams.stream()
                .map(Team::getId)
                .toList();
    }
}
