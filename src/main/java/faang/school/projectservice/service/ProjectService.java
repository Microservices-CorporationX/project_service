package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    List<Filter<Project, ProjectFilterDto>> projectFilters;

    public ProjectDto createSubProject(Long parentId, CreateProjectDto createProjectDto) {

        return null;
    }

    public ProjectDto updateSubProject(Long parentId, UpdateSubProjectDto updateSubProjectDto) {

        return null;
    }

    public List<ProjectDto> filterSubProjects(Long parentId, ProjectFilterDto filters) {

        return null;
    }

}
