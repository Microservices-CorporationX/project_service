package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
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
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "schedule.id", target = "scheduleId")
    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "mapToChildrenIds")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "mapToTaskIds")
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapToResourceIds")
    @Mapping(source = "teams", target = "teamIds", qualifiedByName = "mapToTeamIds")
    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "mapToStageIds")
    @Mapping(source = "vacancies", target = "vacancyIds", qualifiedByName = "mapToVacancyIds")
    @Mapping(source = "moments", target = "momentIds", qualifiedByName = "mapToMomentIds")
    @Mapping(source = "meets", target = "meetIds", qualifiedByName = "mapToMeetsIds")
    ProjectResponseDto toResponseDto(Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Project toEntity(CreateProjectDto projectDto);

    @Named("mapToChildrenIds")
    default List<Long> mapToChildrenIds(List<Project> children) {
        return children == null ? new ArrayList<>() : children.stream().map(Project::getId).toList();
    }

    @Named("mapToTaskIds")
    default List<Long> mapToTaskIds(List<Task> tasks) {
        return tasks == null ? new ArrayList<>() : tasks.stream().map(Task::getId).toList();
    }

    @Named("mapToResourceIds")
    default List<Long> mapToResourceIds(List<Resource> resources) {
        return resources == null ? new ArrayList<>() : resources.stream().map(Resource::getId).toList();
    }

    @Named("mapToTeamIds")
    default List<Long> mapToTeamIds(List<Team> teams) {
        return teams == null ? new ArrayList<>() : teams.stream().map(Team::getId).toList();
    }

    @Named("mapToStageIds")
    default List<Long> mapToStageIds(List<Stage> stages) {
        return stages == null ? new ArrayList<>() : stages.stream().map(Stage::getStageId).toList();
    }
    @Named("mapToVacancyIds")
    default List<Long> mapToVacancyIds(List<Vacancy> vacancies) {
        return vacancies == null ? new ArrayList<>() :  vacancies.stream().map(Vacancy::getId).toList();
    }

    @Named("mapToMomentIds")
    default List<Long> mapToMomentIds(List<Moment> moments) {
        return moments == null ? new ArrayList<>() : moments.stream().map(Moment::getId).toList();
    }

    @Named("mapToMeetsIds")
    default List<Long> mapToMeetsIds(List<Meet> meets) {
        return meets == null ? new ArrayList<>() : meets.stream().map(Meet::getId).toList();
    }
}
