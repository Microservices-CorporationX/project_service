package faang.school.projectservice.controller;


import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.utilities.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.TEAM_MEMBER)
public class TeamController {

    private final TeamMemberService teamMemberService;

    @PostMapping()
    public TeamMemberDto addTeamMemberInTeam(@RequestBody TeamMemberDto teamMemberDto, @RequestBody List<ResourceType> allowedResourceTypes) {
        if (validateTeamMemberList(teamMemberDto)) {
            return teamMemberService.addTeamMemberInTeam(teamMemberDto, allowedResourceTypes);
        } else throw new IllegalArgumentException("Invalid team member credentials");
    }

    @PutMapping("/add/{teamMemberid}")
    public TeamMemberDto addTeamRole(@PathVariable Long teamMemberId, @RequestParam TeamRole role) {
        validateTeamRole(role);
        return teamMemberService.addTeamRole(teamMemberId, role);
    }

    @PutMapping("/remove/{teamMemberid}")
    public TeamMemberDto removeTeamRole(@PathVariable Long teamMemberId, @RequestParam TeamRole role) {
        validateTeamRole(role);
        return teamMemberService.removeTeamRole(teamMemberId, role);
    }

    @DeleteMapping("/{teamMemberid}")
    public void removeTeamMember(@PathVariable Long teamMemberId, @RequestParam Long projectId) {
        teamMemberService.removeTeamMember(teamMemberId, projectId);
    }

    @GetMapping("/all")
    public List<TeamMemberDto> getAllTeamMembers() {
        return teamMemberService.getAllTeamMembers();
    }

    @GetMapping()
    public TeamMemberDto getTeamMemberById(Long id) {
        return teamMemberService.getMemberById(id);
    }

    @GetMapping("/{projectId}")
    public List<TeamMemberDto> getTeamMembersByProjectName(@PathVariable Long projectId, @RequestParam TeamRole role) {
        validateTeamRole(role);
        return teamMemberService.getTeamMembersByProjectName(projectId, role);
    }

    private boolean validateTeamMemberList(TeamMemberDto teamMemberDto) {
        return teamMemberDto.getId() != null && teamMemberDto.getTeamId() != null
                && teamMemberDto.getUserId() != null;
    }

    private boolean validateTeamRole(TeamRole role) {
        return !role.name().isBlank();
    }

}
