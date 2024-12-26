package faang.school.projectservice.mapper.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    @Mapping(source = "team.id", target = "teamId")
    TeamMemberDto toDto(TeamMember teamMember);

    @Mapping(source = "teamId", target = "team.id")
    TeamMember toEntity(TeamMemberDto teamMemberDto);

    @Mapping(source = "team.id", target = "teamId")
    List<TeamMemberDto> toDtoList(List<TeamMember> teamMembers);

    @Mapping(source = "teamId", target = "team.id")
    List<TeamMember> toEntityList(List<TeamMemberDto> teamMemberDtos);
}
