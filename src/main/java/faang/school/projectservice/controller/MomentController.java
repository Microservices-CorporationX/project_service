package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.ProjectDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping("/create")
    public MomentDto createMoment(MomentDto momentDto) {
        if (momentDto.getName() == null || momentDto.getProjectDtos().isEmpty()) {
            return momentDto;
        }

        return momentService.createMoment(momentDto);
    }

    @PostMapping("/update")
    public MomentDto updateMoment(MomentDto momentDto) {
        if (momentDto == null || momentDto.getId() == null) {
            return momentDto;
        }

        return momentService.updateMoment(momentDto);
    }

    @GetMapping
    public List<MomentDto> getProjectMoments(ProjectDto projectDto, MomentFilterDto momentFilterDto) {
        if (projectDto == null || momentFilterDto == null) {
            return List.of();
        }

        return momentService.getProjectMoments(projectDto, momentFilterDto);
    }

    @GetMapping
    public List<MomentDto> getProjectMoments(ProjectDto projectDto) {
        if (projectDto == null) {
            return List.of();
        }

        return momentService.getProjectMoments(projectDto);
    }

    @GetMapping("/{id}")
    public MomentDto getMomentById(@PathVariable Long id) {
        return momentService.getMomentById(id);
    }
}
