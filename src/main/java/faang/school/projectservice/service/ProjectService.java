package faang.school.projectservice.service;

import faang.school.projectservice.model.ProjectStatus;

public interface ProjectService {
    ProjectStatus getProjectStatus(Long id);
}
