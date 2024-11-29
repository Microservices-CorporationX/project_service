package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(source = "teams", target = "teamIds", qualifiedByName = "mapTeamsIds")
    ResponseProjectDto toResponseDto(Project project);

    @Mapping(target = "teams", ignore = true)
    Project toEntity(ResponseProjectDto responseProjectDto);

    List<ResponseProjectDto> toResponseDto(List<Project> projects);
    List<Project> toEntity(List<ResponseProjectDto> responseProjectDtos);

    @Named("mapTeamsIds")
    default List<Long> mapCandidatesIds(List<Team> teams) {
        return teams.stream()
                .map(Team::getId)
                .toList();
    }
}
