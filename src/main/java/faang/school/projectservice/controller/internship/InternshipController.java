package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/internships")
    public ResponseEntity<InternshipDto> createInternship(@RequestBody InternshipDto internshipDto) {
        InternshipDto responseDto = internshipService.createInternship(internshipDto);
        URI location = URI.create("/api/v1/internships/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/internships")
    public ResponseEntity<InternshipDto> updateInternship(@RequestBody @Valid InternshipDto internshipDto) {
        InternshipDto responseDto = internshipService.updateInternship(internshipDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/internships")
    public ResponseEntity<List<InternshipDto>> getInternships() {
        List<InternshipDto> internships = internshipService.getInternships();
        return ResponseEntity.ok(internships);
    }

    @GetMapping("/filters/internships")
    public ResponseEntity<List<InternshipDto>> getInternships(@RequestBody @Valid InternshipFilterDto filters) {
        List<InternshipDto> internships = internshipService.getInternships(filters);
        return ResponseEntity.ok(internships);
    }

    @GetMapping("/{internshipId}/internships")
    public ResponseEntity<InternshipDto> getInternship(@PathVariable @Valid Long internshipId) {
        InternshipDto responseDto = internshipService.getInternship(internshipId);
        return ResponseEntity.ok(responseDto);
    }
}
