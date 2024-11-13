package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    StageDto toStageDto(Stage stage);

    Stage toStage(StageDto stageDto);

    StageDto.StageRolesDto toStageRolesDto(StageRoles stageRoles);

    StageRoles toStageRoles(StageDto.StageRolesDto stageRolesDto);

}
