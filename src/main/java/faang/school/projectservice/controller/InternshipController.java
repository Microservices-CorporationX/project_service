package faang.school.projectservice.controller;

import faang.school.projectservice.docs.internship.CreateInternshipDoc;
import faang.school.projectservice.docs.internship.FilterInternshipDoc;
import faang.school.projectservice.docs.internship.GetAllInternshipDoc;
import faang.school.projectservice.docs.internship.GetInternshipDoc;
import faang.school.projectservice.docs.internship.UpdateInternshipDoc;
import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipGetAllDto;
import faang.school.projectservice.dto.internship.InternshipGetByIdDto;
import faang.school.projectservice.dto.internship.InternshipUpdatedDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.InternshipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/internships")
@Tag(name = "Internship", description = "Internship operations")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    @CreateInternshipDoc
    public InternshipCreatedDto createInternship(@Valid @RequestBody InternshipCreatedDto internship) {
        log.info("Creating internship '{}' by UserId {}.", internship.getName(), internship.getCreatedBy());
        return internshipService.createInternship(internship);
    }

    @PutMapping
    @UpdateInternshipDoc
    public InternshipUpdatedDto updateInternship(@Valid @RequestBody InternshipUpdatedDto internship) {
        log.info("Updating internship '{}' by UserId: {}.", internship.getId(), internship.getCreatedBy());
        return internshipService.updateInternship(internship);
    }

    @GetMapping("/filters")
    @FilterInternshipDoc
    public List<InternshipFilterDto> getAllInternshipsByFilter(
            @NotNull(message = "Internship status can not be null")
            @RequestParam(required = false) InternshipStatus internshipStatus,
            @NotNull(message = "Team role can not be null")
            @RequestParam(required = false) TeamRole teamRole,
            @NotNull(message = "Created by can not be null")
            @RequestParam(required = false) Long createdBy
    ) {
        InternshipFilterDto filterInternship = new InternshipFilterDto(internshipStatus, teamRole, createdBy);

        log.info("Filtering all internships by User {}.", filterInternship.getCreatedBy());

        return internshipService.filterInternship(filterInternship);
    }

    @GetMapping
    @GetAllInternshipDoc
    public List<InternshipGetAllDto> getAllInternship() {
        log.info("Getting all internships.");
        List<InternshipGetAllDto> result = internshipService.getAllInternships();
        log.info("Found {} internships", result.size());
        return result;
    }

    @GetInternshipDoc
    @GetMapping("/{internshipId}")
    public InternshipGetByIdDto getInternshipById(
            @PathVariable
            @Positive(message = "InternshipId must be positive")
            long internshipId) {
        log.info("Getting internship by Id {}", internshipId);
        return internshipService.getByIdInternship(internshipId);
    }
}
