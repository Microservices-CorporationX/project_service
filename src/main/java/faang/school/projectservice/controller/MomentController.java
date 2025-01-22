package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentCreateDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentGetDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MomentController {
    private final MomentService momentService;

    public MomentCreateDto createMoment(@Valid @RequestBody MomentCreateDto momentDto) {
        return momentService.create(momentDto);
    }

    public MomentUpdateDto updateMoment(@Valid @RequestBody MomentUpdateDto momentDto) {
        return momentService.update(momentDto);
    }

    public List<MomentGetDto> getFilteredMoments(@RequestBody MomentFilterDto filters) {
        return momentService.getFilteredMoments(filters);
    }

    public List<MomentGetDto> getAllMoments() {
        return momentService.getMoments();
    }

    public MomentGetDto getMoment(@Valid @PathVariable @Positive long id) {
        return momentService.getMoment(id);
    }
}
