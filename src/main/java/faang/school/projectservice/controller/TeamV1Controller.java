package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateTeamMemberDto;
import faang.school.projectservice.dto.UpdateTeamMemberDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@RestController
public class TeamV1Controller {
    private final TeamMemberService teamService;

    @PostMapping("/{creatorId}/members")
    public CreateTeamMemberDto addTeamMember(@RequestBody @Valid CreateTeamMemberDto teamMemberDto,
                                             @PathVariable @Positive Long creatorId) {
        return teamService.addTeamMember(teamMemberDto, creatorId);
    }

    @PostMapping("/{updaterId}/members/{memberId}")
    public UpdateTeamMemberDto updateTeamMember(@RequestBody @Valid UpdateTeamMemberDto teamMemberDto,
                                                @PathVariable @Positive Long updaterId) {
        return teamService.updateTeamMember(teamMemberDto, updaterId);
    }

    @DeleteMapping("/{deleterId}/members/{memberId}")
    public void deleteTeamMember(@PathVariable @Positive Long deleterId,
                                 @PathVariable @Positive Long memberId) {
        teamService.deleteTeamMember(memberId, deleterId);
    }

    @GetMapping("/{projectId}/members")
    public List<CreateTeamMemberDto> getFilteredTeamMembers(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) TeamRole role,
                                                            @PathVariable @Positive Long projectId) {
        return teamService.getFilteredTeamMembers(name, role, projectId);
    }

    @GetMapping("/members")
    public List<CreateTeamMemberDto> getAllTeamMembers() {
        return teamService.getAllTeamMembers();
    }

    @GetMapping("/{teamId}/members")
    public List<CreateTeamMemberDto> getMembersByTeamId(@PathVariable @Positive Long teamId) {
        return teamService.getTeamMembersByTeamId(teamId);
    }
}