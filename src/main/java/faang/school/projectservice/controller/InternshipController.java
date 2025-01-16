package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.InternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        validateInternshipCreationRequest(internshipDto);
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/{id}")
    public InternshipDto updateInternship(@PathVariable Long id, @RequestBody InternshipDto internshipDto) {
        internshipDto.setId(id);
        return internshipService.updateInternship(internshipDto);
    }

    @GetMapping("/{id}")
    public InternshipDto getInternshipById(@PathVariable Long id) {
        return internshipService.getInternshipById(id);
    }

    @GetMapping
    public List<InternshipDto> getInternships(
            @RequestParam(required = false) InternshipStatus status,
            @RequestParam(required = false) Long roleId
    ) {
        return internshipService.getInternships(status, roleId);
    }

    private void validateInternshipCreationRequest(InternshipDto internshipDto) {
        if (internshipDto.getInternIds() == null || internshipDto.getInternIds().isEmpty()) {
            throw new IllegalArgumentException("Internship must have at least one intern.");
        }
        if (internshipDto.getStartDate() == null || internshipDto.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required.");
        }
    }
}