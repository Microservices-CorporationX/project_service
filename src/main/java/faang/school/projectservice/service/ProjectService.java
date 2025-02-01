package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectService {
    boolean isUserInProject(Long userId, Long projectId);
    boolean isProjectPublic(Long projectId);
    List<Long> getProjectResourceIds(Long projectId);
    Project getProject(Long projectId);
}
