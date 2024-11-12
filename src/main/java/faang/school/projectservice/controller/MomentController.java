package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.ProjectDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public List<MomentDto> getProjectMoments(ProjectDto projectDto, MomentFilterDto momentFilterDto) {
        if (projectDto == null || momentFilterDto == null) {
            return List.of();
        }

        return momentService.getProjectMoments(projectDto, momentFilterDto);
    }

    @GetMapping
    public List<MomentDto> getMoments(ProjectDto projectDto) {
        /**
         * Получаем все моменты по id их проекта
         */
        if (projectDto == null) {
            return List.of();
        }

        return momentService.getProjectMoments(projectDto);
    }
}
