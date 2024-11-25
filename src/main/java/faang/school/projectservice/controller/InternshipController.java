package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    public ResponseEntity<Long> createInternship(@Valid @RequestBody InternshipDto internshipDto) {
        return ResponseEntity.ok(internshipService.createInternship(internshipDto));
    }

    @PutMapping
    public ResponseEntity<Void> updateInternship(@Valid @RequestBody InternshipDto internshipDto) {
        internshipService.updateInternship(internshipDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{internshipId}/complete/{teamMemberId}")
    public ResponseEntity<Void> earlyCompletionInternship(@RequestParam @Positive long internshipId, @RequestParam @Positive long teamMemberId) {
        internshipService.completeInternship(internshipId, teamMemberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{internshipId}/dismissal/{teamMemberId}")
    public ResponseEntity<Void> earlyDismissalFromInternship(@RequestParam @Positive long internshipId, @RequestParam @Positive long teamMemberId) {
        internshipService.dismissTeamMember(internshipId, teamMemberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{internshipId/change/{status}")
    public ResponseEntity<Void> changeInternshipStatus(@RequestParam @Positive long internshipId, @RequestParam InternshipStatus status) {
        internshipService.changeInternshipStatus(internshipId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/filter/{internshipFilterDto}")
    public ResponseEntity<List<InternshipDto>> getFilteredInternships(@RequestBody InternshipFilterDto internshipFilterDto) {
        return ResponseEntity.ok(internshipService.getFilteredInternships(internshipFilterDto));
    }

    @GetMapping
    public ResponseEntity<List<InternshipDto>> getAllInternships() {
        return ResponseEntity.ok(internshipService.getAllInternships());
    }

    @GetMapping("/{internshipId}")
    public ResponseEntity<InternshipDto> getInternshipById(@RequestParam long internshipId) {
        return ResponseEntity.ok(internshipService.getInternshipDtoById(internshipId));
    }

}
