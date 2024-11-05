package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRoleDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "roles")
    @Mapping(source = "executors", target = "executorIds")
    StageDto toDto(Stage stage);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toEntity(StageDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", ignore = true)
    StageRoles toStageRoles(StageRoleDto dto);

    StageRoleDto toStageRoleDto(StageRoles stageRoles);

    default Long map(TeamMember value) {
        return value.getId();
    }
}