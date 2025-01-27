package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectService {
    List<Project> getProjectsByIds (List<Long> projectIds);
}
