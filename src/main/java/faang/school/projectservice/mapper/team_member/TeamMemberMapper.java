package faang.school.projectservice.mapper.team_member;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    @Mapping(target = "team", expression = "java(mapTeamToLong(teamMember.getTeam()))")
    TeamMemberDto toDto(TeamMember teamMember);

    @Mapping(target = "team", expression = "java(mapLongToTeam(teamMemberDto.getTeam()))")
    TeamMember toEntity(TeamMemberDto teamMemberDto);

    default Long mapTeamToLong(Team team) {
        return team != null ? team.getId() : null;
    }

    default Team mapLongToTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        Team team = new Team();
        team.setId(teamId);
        return team;
    }
}
