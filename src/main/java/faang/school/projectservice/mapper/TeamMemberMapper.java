package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateTeamMemberDto;
import faang.school.projectservice.dto.UpdateTeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "mapStageIds")
    CreateTeamMemberDto toCreateDto(TeamMember teamMember);
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "stages", ignore = true)
    TeamMember toCreateEntity(CreateTeamMemberDto createTeamMemberDto);

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "mapStageIds")
    UpdateTeamMemberDto toUpdateDto(TeamMember teamMember);
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "stages", ignore = true)
    TeamMember toUpdateEntity(UpdateTeamMemberDto updateTeamMemberDto);

    List<CreateTeamMemberDto> toCreateDtoList(List<TeamMember> teamMembers);
    List<TeamMember> toCreateEntityList(List<CreateTeamMemberDto> createTeamMemberDtos);

    List<UpdateTeamMemberDto> toUpdateDtoList(List<TeamMember> teamMembers);
    List<TeamMember> toUpdateEntityList(List<UpdateTeamMemberDto> updateTeamMemberDtos);

    @Named("mapStageIds")
    default List<Long> mapStageIds(List<Stage> stages) {
        return stages.stream()
                .map(Stage::getStageId)
                .toList();
    }
}
