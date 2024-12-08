package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "stageRolesDto", target = "stageRoles",
            qualifiedByName = "toEntityStageRolesDto")
    Stage toEntity(StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRolesDto",
            qualifiedByName = "toDtoStageRoles")
    StageDto toDto(Stage stage);

    List<Stage> toEntity(List<StageDto> stageDto);

    List<StageDto> toDto(List<Stage> stage);

    void update(StageDto stageDto, @MappingTarget Stage stage);

    @Named("toDtoStageRoles")
    default StageRolesDto toDtoStageRoles(StageRoles stageRoles) {
        if (stageRoles == null) {
            return null;
        }

        StageRolesDto stageRolesDto = new StageRolesDto();

        stageRolesDto.setId(stageRoles.getId());
        stageRolesDto.setTeamRole(stageRoles.getTeamRole());
        stageRolesDto.setCount(stageRoles.getCount());

        return stageRolesDto;
    }

    @Named("toEntityStageRolesDto")
    default StageRoles toEntityStageRolesDto(StageRolesDto stageRolesDto) {
        if ( stageRolesDto == null ) {
            return null;
        }

        StageRoles.StageRolesBuilder stageRoles = StageRoles.builder();

        stageRoles.id( stageRolesDto.getId() );
        stageRoles.teamRole( stageRolesDto.getTeamRole() );
        stageRoles.count( stageRolesDto.getCount() );

        return stageRoles.build();
    }
}

