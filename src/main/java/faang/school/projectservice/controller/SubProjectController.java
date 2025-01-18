package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        return null;
    }

    public ProjectDto update(ProjectDto projectDto) {
        return null;
    }

    public List<ProjectDto> create(ProjectFilterDto projectFilterDto) {
        return null;
    }

}
