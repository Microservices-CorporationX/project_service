package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;

    @Value("${api.version}")
    private String apiVersion;

    @PostMapping
    public ResponseEntity<InternshipDto> createInternship(@RequestBody InternshipDto internshipDto) {
        InternshipDto responseDto = internshipService.createInternship(internshipDto);
        URI location = URI.create(apiVersion + "/internships/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping
    public InternshipDto updateInternship(@RequestBody @Valid InternshipDto internshipDto) {
        return internshipService.updateInternship(internshipDto);
    }

    @GetMapping
    public List<InternshipDto> getInternships() {
        return internshipService.getInternships();
    }

    @GetMapping("/filters")
    public List<InternshipDto> getInternships(@RequestParam(required = false) InternshipStatus statusPattern,
                                              @RequestParam(required = false) TeamRole rolePattern) {
        InternshipFilterDto filters = new InternshipFilterDto(statusPattern, rolePattern);
        return internshipService.getInternships(filters);
    }

    @GetMapping("/{internshipId}")
    public InternshipDto getInternship(@PathVariable @Valid Long internshipId) {
        return internshipService.getInternship(internshipId);
    }
}
