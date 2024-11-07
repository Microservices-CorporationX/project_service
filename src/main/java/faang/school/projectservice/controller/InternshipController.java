package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.service.internship.InternshipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Internships.", description = "API for managing internships.")
@RequestMapping("/internship")
@RestController
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping("/create")
    public ResponseEntity<InternshipDto> createInternship(@RequestBody @Valid InternshipCreationDto internshipCreationDto) {
        InternshipDto internshipDto = internshipService.createInternship(internshipCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(internshipDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<InternshipUpdateRequestDto> updateInternship(@RequestBody @Valid InternshipUpdateDto updateDto) {
        InternshipUpdateRequestDto internshipDto = internshipService.updateInternship(updateDto);
        return ResponseEntity.ok(internshipDto);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<InternshipDto>> getFilteredInternships(@RequestBody InternshipFilterDto filterDto) {
        List<InternshipDto> filteredInternships = internshipService.getFilteredInternships(filterDto);
        return ResponseEntity.ok(filteredInternships);
    }

    @GetMapping
    public ResponseEntity<List<InternshipDto>> getAllInternships() {
        List<InternshipDto> filteredInternships = internshipService.getAllInternships();
        return ResponseEntity.ok(filteredInternships);
    }

    @GetMapping("/{internshipId}")
    public ResponseEntity<InternshipDto> getInternshipById(@PathVariable @Positive long internshipId) {
        InternshipDto internship = internshipService.getInternshipById(internshipId);
        return ResponseEntity.ok(internship);
    }
}
