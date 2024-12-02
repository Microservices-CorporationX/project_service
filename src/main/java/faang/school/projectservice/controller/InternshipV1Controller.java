package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internships")
public class InternshipV1Controller {
    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/update/{internshipId}")
    public ResponseEntity<Void> updateInternship(@Valid @RequestBody InternshipDto internshipDto) {
        internshipService.updateInternship(internshipDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{internshipId}/complete/{teamMemberId}")
    public ResponseEntity<Void> earlyCompletionInternship(@RequestParam @Positive long internshipId, @RequestParam @Positive long teamMemberId) {
        internshipService.completeInternship(internshipId, teamMemberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{internshipId}/dismissal/{teamMemberId}")
    public ResponseEntity<Void> earlyDismissalFromInternship(@RequestParam @Positive long internshipId, @RequestParam @Positive long teamMemberId) {
        internshipService.dismissTeamMember(internshipId, teamMemberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{internshipId}/change/{status}")
    public ResponseEntity<Void> changeInternshipStatus(@RequestParam @Positive long internshipId, @RequestParam InternshipStatus status) {
        internshipService.changeInternshipStatus(internshipId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/filter")
    public List<InternshipDto> getFilteredInternships(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.getFilteredInternships(internshipFilterDto);
    }

    @GetMapping
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternshipById(@RequestParam long internshipId) {
        return internshipService.getInternshipDtoById(internshipId);
    }

}
