package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
@Mapper(componentModel = "spring", uses = TeamMemberMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    @Mapping(source = "projectId", target = "project.id")
    Team toEntity(TeamDto teamDto);

    @Mapping(source = "project.id", target = "projectId")
    TeamDto toDto(Team team);

    @Mapping(source = "project.id", target = "projectId")
    List<TeamDto> toDtoList(List<Team> teams);

}
