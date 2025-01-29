package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentCreateDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentReadDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    public MomentReadDto createMoment(@Valid @RequestBody MomentCreateDto momentDto) {
        return momentService.create(momentDto);
    }

    public MomentReadDto updateMoment(@Valid @RequestBody MomentUpdateDto momentDto) {
        return momentService.update(momentDto);
    }

    public List<MomentReadDto> getFilteredMoments(@Valid @RequestBody MomentFilterDto filters) {
        return momentService.getFilteredMoments(filters);
    }

    public List<MomentReadDto> getAllMoments() {
        return momentService.getMoments();
    }

    public MomentReadDto getMoment(@Valid @PathVariable @Positive long id) {
        return momentService.getMoment(id);
    }
}
