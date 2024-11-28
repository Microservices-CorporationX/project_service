package faang.school.projectservice.mapper.managingTeamMapper;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface managingTeamMapper {

    @Mapping(source = "team.id", target = "team")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToStrings")
    @Mapping(source = "nickname", target = "username")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "accessLevel", target = "accessLevel")
    TeamMemberDto toDto(TeamMember teamMember);

    @Mapping(source = "team", target = "team.id")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapStringsToRoles")
    @Mapping(source = "username", target = "nickname")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "accessLevel", target = "accessLevel")
    TeamMember toEntity(TeamMemberDto TeamMemberDto);

    @Named("mapRolesToStrings")
    default List<String> mapRolesToStrings(List<TeamRole> roles) {
        return roles != null
                ? roles.stream().map(Enum::name).collect(Collectors.toList())
                : null;
    }

    @Named("mapStringsToRoles")
    default List<TeamRole> mapStringsToRoles(List<String> roles) {
        return roles != null
                ? roles.stream().map(TeamRole::valueOf).collect(Collectors.toList())
                : null;
    }
}