package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.filter.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
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

    @PutMapping
    public ResponseEntity<MomentDto> updateMoment(@Valid @RequestBody MomentDto momentDto) {
        return ResponseEntity.ok(momentService.updateMoment(momentDto));
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
