package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectResponseDto;

import java.util.List;

public interface ProjectService {
    List<ProjectResponseDto> getProjectsByIds (List<Long> projectIds);
}
