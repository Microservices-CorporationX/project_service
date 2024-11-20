package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResponseTeamMemberDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectV1Controller {
    private final TeamMemberService teamService;

    @GetMapping("/{projectId}/members")
    public List<ResponseTeamMemberDto> getFilteredTeamMembers(@RequestParam(required = false) String name,
                                                              @RequestParam(required = false) TeamRole role,
                                                              @PathVariable @Positive Long projectId) {
        return teamService.getFilteredTeamMembers(name, role, projectId);
    }
}
