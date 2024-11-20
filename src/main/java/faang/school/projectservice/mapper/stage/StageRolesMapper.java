package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    StageRoles toEntity(StageRolesDto stageRolesDto);

    List<StageRoles> toEntity(List<StageRolesDto> stageRoles);

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRolesDto toDto(StageRoles stageRoles);
}
