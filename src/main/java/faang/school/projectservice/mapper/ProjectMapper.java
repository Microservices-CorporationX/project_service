package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "teams", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "tasksToIds")
    @Mapping(source = "teams", target = "teamIds", qualifiedByName = "teamsToIds")
    ProjectDto toDto(Project project);

    @Named("tasksToIds")
    default List<Long> tasksToIds(List<Task> taskList) {
        return taskList.stream().map(Task::getId).toList();
    }

    @Named("teamsToIds")
    default List<Long> teamsToIds(List<Team> teamList) {
        return teamList.stream().map(Team::getId).toList();
    }

}
