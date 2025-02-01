package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageMapper {


    Stage toStage(StageDto stageDto);

    StageDto toStageDto(Stage stage);

    StageUpdateDto toStageUpdateDto(StageDto stageDto);

    StageDto toStageDto(StageUpdateDto stageUpdateDto);
}
