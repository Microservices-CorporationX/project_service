package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "newProject.name")
    @Mapping(target = "description", source = "newProject.description")
    @Mapping(target = "parentProject", source = "parentProject")
    @Mapping(target = "ownerId", source = "newProject.ownerId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "visibility", source = "parentProject.visibility")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "children", expression = "java(java.util.List.of())")
    Project toNewEntity(Project newProject, Project parentProject);

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toProjectsList(List<Project> projectsList);

}
