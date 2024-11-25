package faang.school.projectservice.controller.momentController;

import faang.school.projectservice.dto.momentDto.MomentDto;
import faang.school.projectservice.dto.momentDto.MomentFilterDto;
import faang.school.projectservice.service.momentService.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moments")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MomentController {
    private final MomentService momentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MomentDto create(@Validated @RequestBody MomentDto momentDto) {
        log.info("Received a request to create a moment with ID: {}", momentDto.getId());
        return momentService.create(momentDto);
    }

    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    public MomentDto update(@RequestParam Long id, @RequestParam Long userId, @RequestParam Long projectId) {
        log.info("Received a request to update a moment with ID: {}", id);
        return momentService.update(id, userId, projectId);
    }

    @GetMapping("/filtered")
    @ResponseStatus(HttpStatus.OK)
    public List<MomentDto> getMomentsByFilters(@RequestBody MomentFilterDto filters) {
        log.info("Received a request to get moment by filters: {}", filters);
        return momentService.getMomentsByFilter(filters);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<MomentDto> getMoments() {
        log.info("Received a request to get moment list");
        return momentService.getMoments();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MomentDto getMomentById(@PathVariable Long id) {
        log.info("Received a request to get moment by id: {}", id);
        return momentService.getMomentById(id);
    }
}
