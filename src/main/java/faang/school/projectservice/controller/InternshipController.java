package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createInternship(@Valid @RequestBody InternshipDto internshipDto) {
        internshipService.createInternship(internshipDto);
    }

    @PutMapping
    public void updateInternship(@Valid @RequestBody InternshipDto internshipDto) {
        internshipService.updateInternship(internshipDto);
    }

    @PostMapping("/{internshipId}/complete/{teamMemberId}")
    @ResponseStatus(HttpStatus.OK)
    public void earlyCompletionInternship(@RequestParam long internshipId, @RequestParam long teamMemberId) {
        internshipService.completeInternship(internshipId, teamMemberId);
    }

    @PostMapping("/{internshipId}/dismissal/{teamMemberId}")
    @ResponseStatus(HttpStatus.OK)
    public void earlyDismissalFromInternship(@RequestParam long internshipId,@RequestParam long teamMemberId) {
        internshipService.dismissTeamMember(internshipId, teamMemberId);
    }

    @GetMapping("/filter")
    public List<InternshipDto> getFilteredInternships(@RequestParam InternshipStatus internshipStatus) {
        return internshipService.getFilteredInternships(internshipStatus);
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
