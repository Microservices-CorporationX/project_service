package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
     TeamMember toEntity(TeamMemberDto teamMemberDto);

     TeamMemberDto toDto (TeamMember teamMember);

     List<TeamMember> toEntityList(List<TeamMemberDto> teamMemberDtoList);

     List<TeamMemberDto> toDtoList (List<TeamMember> teamMemberList);
}
