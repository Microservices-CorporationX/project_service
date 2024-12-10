package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "teamMembers", ignore = true)
    Team toEntity(TeamDto teamDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "teamMembers", target = "teamMemberIds", qualifiedByName = "mapToTeamMemberIds")
    TeamDto toDto(Team team);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "teamMembers", target = "teamMemberIds", qualifiedByName = "mapToTeamMemberIds")
    List<TeamDto> mapToDtoList(List<Team> teams);

    @Named("mapToTeamMemberIds")
    default List<Long> map(List<TeamMember> members) {
        return members.stream().map(TeamMember::getId).toList();
    }
}
