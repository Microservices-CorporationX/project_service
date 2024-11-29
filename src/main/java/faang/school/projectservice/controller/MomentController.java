package faang.school.projectservice.controller;

import faang.school.projectservice.docs.moment.AllMomentsDoc;
import faang.school.projectservice.docs.moment.FiltersMomentDoc;
import faang.school.projectservice.docs.moment.GetMomentDoc;
import faang.school.projectservice.docs.moment.SaveMomentDoc;
import faang.school.projectservice.docs.moment.UpdateMomentDoc;
import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/moments")
@Tag(name = "Moments", description = "Endpoints for moments")
public class MomentController {

    private final MomentService momentService;

    @PostMapping
    @SaveMomentDoc
    public ResponseEntity<MomentDto> saveMoment(@Valid @RequestBody MomentDto momentDto) {
        MomentDto savedMoment = momentService.saveMoment(momentDto);
        log.info("Moment saved: {}", savedMoment);

        return ResponseEntity.status(HttpStatus.SC_CREATED).body(savedMoment);
    }

    @PutMapping
    @UpdateMomentDoc
    public ResponseEntity<MomentDto> updateMomentWithParthner(@Valid @RequestBody MomentDto momentDto) {
        MomentDto updateMoment = momentService.updateMoment(momentDto);
        log.info("Moment updated: {}", updateMoment);

        return ResponseEntity.ok(updateMoment);
    }

    @FiltersMomentDoc
    @GetMapping("/filters")
    public ResponseEntity<List<MomentDto>> getMomentsWithFilter(@Valid @RequestParam MomentFilterDto filterDto) {
        List<MomentDto> momentDtos = momentService.getMoments(filterDto);
        if (momentDtos.isEmpty()) {
            log.info("Moments not found");
            return ResponseEntity.notFound().build();
        }
        log.info("Found {} moments", momentDtos.size());

        return ResponseEntity.ok(momentDtos);
    }

    @GetMapping
    @AllMomentsDoc
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        List<MomentDto> momentDtos = momentService.getAllMoments();
        if (momentDtos.isEmpty()) {
            log.info("Moments not found");
            return ResponseEntity.notFound().build();
        }
        log.info("Found {} moments", momentDtos.size());

        return ResponseEntity.ok(momentDtos);
    }

    @GetMomentDoc
    @GetMapping("/{momentId}")
    public ResponseEntity<MomentDto> getMoment(@PathVariable @NotNull @Positive long momentId) {
        MomentDto momentDto = momentService.getMoment(momentId);
        log.info("Moment found by id {}", momentId);

        return ResponseEntity.ok(momentDto);
    }
}
