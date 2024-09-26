package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.mapper.util.StageMapperUtil;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageMapperUtil.class)
public interface StageMapper {
    @Mapping(target = "projectId", source = "project.id")
    StageDto toDto(Stage stage);

    @Mapping(target = "project", source = "projectId", qualifiedByName = "getProjectById")
    @Mapping(target = "executors", source = "executorsDtos", qualifiedByName = "getExecutors")
    Stage toStage(StageDto stageDto);

    List<StageDto> toStageDtoList(List<Stage> stages);

    List<StageRolesDto> toStageRolesDtoList(List<StageRoles> roles);

    List<TeamMemberDto> toExecutorDtos(List<TeamMember> members);
}
