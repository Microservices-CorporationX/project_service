package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.service.MomentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RestController
@RequestMapping("api/v1/moments")
@RequiredArgsConstructor
@Validated
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public void saveMoment(@Valid @RequestBody MomentDto momentDto) {
        momentService.saveMoment(momentDto);
    }

    @PutMapping
    public void updateMomentWithParthner(@Valid @RequestBody MomentDto momentDto) {
        momentService.updateMoment(momentDto);
    }

    @GetMapping("/filtered")
    public List<MomentDto> getMomentsWithFilter(@Valid MomentFilterDto filterDto) {
        return momentService.getMoments(filterDto);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{id}")
    public MomentDto getMoment(@Valid @PathVariable("id") long momentId) {
        return momentService.getMoment(momentId);
    }
}
