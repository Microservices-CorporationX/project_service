package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageRolesMapper.class)
public interface StageMapper {
    Stage toEntity(StageDto stageDto);
    StageDto toDto(Stage stage);
    List<Stage> toEntity(List<StageDto> stageDtos);
    List<StageDto> toDto(List<Stage> stages);
    void update(StageDto stageDto, @MappingTarget Stage stage);
}
