package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageRolesMapper {
    StageRoles toEntity(StageRolesDto stageRoleDto);

    StageRolesDto toDto(StageRoles stageRole);
}
