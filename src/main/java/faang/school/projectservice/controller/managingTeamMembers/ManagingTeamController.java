package faang.school.projectservice.controller.managingTeamMembers;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.service.managingTeamService.managingTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/managing-team")
public class ManagingTeamController {

    private final managingTeamService ManagingTeamService;

    @PostMapping()
    public TeamMemberDto addTeamMember(@PathVariable Long projectId, @Valid @RequestBody TeamMemberDto TeamMemberDto, @PathVariable Long teamMemberId) {
        return ManagingTeamService.addTeamMember(projectId, TeamMemberDto, teamMemberId);
    }

    @PutMapping()
    public TeamMemberDto updateTeamMember(@PathVariable Long projectId, @Valid @RequestBody TeamMemberDto TeamMemberDto, @PathVariable Long teamMemberId, @PathVariable Long currentUserId) {
        return ManagingTeamService.updateTeamMember(projectId, TeamMemberDto, teamMemberId, currentUserId);
    }

    @DeleteMapping("/{teamMemberId}")
    public TeamMemberDto deleteTeamMember(@PathVariable Long projectId, @PathVariable Long teamMemberId,  Long currentUserId) {
        return ManagingTeamService.deleteTeamMember(projectId, teamMemberId, currentUserId);
    }

    @GetMapping()
    public List<TeamMemberDto> getTeamMembers(@PathVariable Long projectId, @Valid @RequestBody TeamMemberFilterDto teamMemberFilterDto) {
        return ManagingTeamService.getTeamMemberWithFilter(projectId, teamMemberFilterDto);
    }


    @GetMapping("/all")
    public List<TeamMemberDto> getAllTeamMembers(Long projectId) {
        return ManagingTeamService.getAllMembers(projectId);
    }

    @GetMapping("/{teamMemberId}")
    public TeamMemberDto getTeamMember(@PathVariable Long projectId, @PathVariable Long teamMemberId) {
        return ManagingTeamService.getTeamMember(projectId, teamMemberId);
    }

}
