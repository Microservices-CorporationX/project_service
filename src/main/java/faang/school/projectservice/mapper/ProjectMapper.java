package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {
                Task.class,
                Resource.class,
                Team.class,
                Stage.class,
                Vacancy.class,
                Moment.class,
                Meet.class
        })
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "schedule.id", target = "scheduleId")
    @Mapping(target = "tasksIds", expression = "java(mapEntitiesToIds(project.getTasks(), Task::getId))")
    @Mapping(target = "resourcesIds", expression = "java(mapEntitiesToIds(project.getResources(), Resource::getId))")
    @Mapping(target = "teamsIds", expression = "java(mapEntitiesToIds(project.getTeams(), Team::getId))")
    @Mapping(target = "stagesIds", expression = "java(mapEntitiesToIds(project.getStages(), Stage::getStageId))")
    @Mapping(target = "vacanciesIds", expression = "java(mapEntitiesToIds(project.getVacancies(), Vacancy::getId))")
    @Mapping(target = "momentsIds", expression = "java(mapEntitiesToIds(project.getMoments(), Moment::getId))")
    @Mapping(target = "meetsIds", expression = "java(mapEntitiesToIds(project.getMeets(), Meet::getId))")
    ProjectResponseDto toResponseDto(Project project);

    @Named("entitiesToIds")
    default <T> List<Long> mapEntitiesToIds(List<T> entities, Function<T, Long> function) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(function)
                .toList();
    }

    Project toProject(ProjectCreateRequestDto projectCreateRequestDto);

    void update(@MappingTarget Project project, ProjectUpdateRequestDto projectUpdateRequestDto);
}
