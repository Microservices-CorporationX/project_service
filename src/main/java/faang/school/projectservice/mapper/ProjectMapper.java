package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateReq;
import faang.school.projectservice.dto.project.ProjectResp;
import faang.school.projectservice.dto.project.ProjectPatchReq;
import faang.school.projectservice.model.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Project mapProjectCreateReqToProject(ProjectCreateReq projectCreateReq);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchProjectFromProjectPatchReq(ProjectPatchReq projectPatchReq, @MappingTarget Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    ProjectResp mapProjectToProjectResp(Project project);

    List<ProjectResp> mapProjectListToProjectRespList(List<Project> projects);
}
