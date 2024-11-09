package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/moment")
@RequiredArgsConstructor
public class MomentController {
    private final MomentService service;

    @PostMapping("/create")
    public ResponseEntity<MomentDto> createMoment(@RequestBody @Valid MomentDto momentDto) {
        return ResponseEntity.ok(service.createMoment(momentDto));
    }

    @PostMapping("/{momentId}")
    public ResponseEntity<MomentDto> updateMoment(@PathVariable("momentId") Long momentId,
                                                  @RequestBody @Valid MomentDto updatedMomentDto) {
        return ResponseEntity.ok(service.updateMoment(updatedMomentDto, momentId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MomentDto>> getMomentsByFilter(@RequestBody @Valid MomentFilterDto momentFilterDto) {
        return ResponseEntity.ok(service.getMomentsByFilter(momentFilterDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        return ResponseEntity.ok(service.getAllMoments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MomentDto> getMomentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getMomentById(id));
    }

}
