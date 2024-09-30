package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class MomentController {
    private final MomentService momentService;

    public MomentDto createMoment(@Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    public List<MomentDto> getAllProjectMomentsByDate(@NotNull Long projectId, LocalDateTime month) {
        return momentService.getAllProjectMomentsByDate(projectId, month);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto updateMoment(long momentId, List<Long> addedProjectIds, List<Long> addedUserIds) {
        return momentService.updateMoment(momentId, addedProjectIds, addedUserIds);
    }

    public MomentDto getMoment(long momentId) {
        return momentService.getMomentById(momentId);
    }
}
