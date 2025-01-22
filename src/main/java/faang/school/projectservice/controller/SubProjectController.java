package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.SubProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectUpdateDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/subprojects")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectReadDto create(@Valid @RequestBody SubProjectCreateDto createDto) {
        return projectService.create(createDto);
    }

    @PutMapping
    public ProjectReadDto update(@Valid @RequestBody SubProjectUpdateDto updateDto) {
        return projectService.update(updateDto);
    }

    @GetMapping("/{projectId}")
    public List<ProjectReadDto> getSubProjects(@PathVariable long projectId, SubProjectFilterDto filterDto) {
        return projectService.getSubProjects(projectId, filterDto);
    }

}
