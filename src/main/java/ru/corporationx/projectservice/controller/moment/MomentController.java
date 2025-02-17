package ru.corporationx.projectservice.controller.moment;

import ru.corporationx.projectservice.model.dto.filter.MomentFilterDto;
import ru.corporationx.projectservice.model.dto.moment.MomentDto;
import ru.corporationx.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    public MomentDto createMoment(@RequestBody @Valid MomentDto momentDto) {
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/{momentId}")
    public ResponseEntity<MomentDto> updateMoment(@Valid @RequestBody MomentDto momentDto,
                                                  @Positive @PathVariable Long momentId) {
        return ResponseEntity.ok(momentService.updateMoment(momentDto, momentId));
    }

    @GetMapping("/filters")
    public ResponseEntity<List<MomentDto>> getMomentsByFilter(@Valid @RequestBody MomentFilterDto filters){
        return ResponseEntity.ok(momentService.getMomentsByFilter(filters));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MomentDto>> getAllMoments(){
        return ResponseEntity.ok(momentService.getAllMoments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MomentDto> getMomentById(@Positive @PathVariable Long id){
        return ResponseEntity.ok(momentService.getMomentById(id));
    }


}
