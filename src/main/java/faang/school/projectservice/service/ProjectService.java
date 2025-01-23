package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.ProjectDto;

public interface ProjectService {
    ProjectDto getProject(long projectId);
}
