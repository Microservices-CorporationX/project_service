package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;


    public MomentDto create(MomentDto momentDto) {
        validateMoment(momentDto);
        return momentService.create(momentDto);
    }

    public MomentDto update(MomentDto momentDto) {
        return momentService.update(momentDto);
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filterDto) {
        return momentService.getMomentsByFilter(filterDto);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto getMomentById(long id) {
        return momentService.getMomentById(id);
    }


    private void validateMoment(MomentDto momentDto) {
        if (momentDto.getName().isBlank()) {
            throw new ValidationException("The moment doesn't have name");
        }
        if (momentDto.getProjects().isEmpty()) {
            throw new ValidationException("The moment doesn't have related project");
        }
        if (momentDto.getProjects().stream().anyMatch(project -> project.getStatus()
                .equals(ProjectStatus.COMPLETED))) {
            throw new ValidationException("The project was completed");
        }
        if (momentDto.getProjects().stream().anyMatch(project -> project.getStatus()
                .equals(ProjectStatus.CANCELLED))) {
            throw new ValidationException("The project was cancelled");
        }
    }

}
