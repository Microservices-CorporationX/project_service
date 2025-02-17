package ru.corporationx.projectservice.mapper.team;

import ru.corporationx.projectservice.mapper.teammember.TeamMemberMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.corporationx.projectservice.model.dto.team.TeamDto;
import ru.corporationx.projectservice.model.entity.Team;

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
