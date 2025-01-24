package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/create")
    public void createInternship(@RequestBody InternshipDto dto) {
        internshipService.createInternship(dto);
    }

    @PostMapping("/update")
    public void updateInternship(@RequestBody InternshipDto dto) {
        internshipService.updateInternship(dto);
    }

    @PostMapping("/filter-by-status")
    public List<InternshipDto> getFilteredInternshipsByStatus(@RequestBody InternshipStatus status) {
        return internshipService.getFilteredInternshipsByStatus(status);
    }

    @PostMapping("/filter-by-role")
    public List<InternshipDto> getFilteredInternshipsByRole(List<TeamRole> filterRoles) {
        return internshipService.getFilteredInternshipsByRole(filterRoles);
    }

    @GetMapping("/get-all")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/getById")
    public InternshipDto getInternshipById(@RequestParam long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }

    @PutMapping("/{internshipId}/remove-interns")
    public void removeInternFromInternship(
            @RequestBody @Valid List<Long> internIds,
            @PathVariable long internshipId) {
        internshipService.removeInternFromInternship(internIds, internshipId);
    }

    @PutMapping("/{internshipId}/finish-early")
    public void finishTheInternshipAheadOfScheduleFor(
            @RequestBody @Valid List<Long> internIds,
            @PathVariable long internshipId) {
        internshipService.finishTheInternshipAheadOfScheduleFor(internIds, internshipId);
    }
}
