package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "stageRoles", target = "stageRolesId", qualifiedByName = "mapStageToIds")
    @Mapping(source = "executors", target = "executorsId", qualifiedByName = "mapTeamMemberToIds")
    @Mapping(source = "project", target = "projectId", qualifiedByName = "mapProjectToIds")
    StageDto toDto(Stage stage);

    Stage toEntity(StageDto stageDto);

    @Named("mapStageToIds")
    default List<Long> mapStageToIds(List<StageRoles> stages) {
        return stages.stream().map(StageRoles::getId).toList();
    }

    @Named("mapTeamMemberToIds")
    default List<Long> mapTeamMemberToIds(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }

    @Named("mapProjectToIds")
    default Long mapProjectToId(Project project) {
        return project != null ? project.getId() : null;
    }
}
