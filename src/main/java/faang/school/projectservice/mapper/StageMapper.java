package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageMapper {

    Stage toEntity(StageDto stageDto);

    StageDto toDto(Stage stage);
}
