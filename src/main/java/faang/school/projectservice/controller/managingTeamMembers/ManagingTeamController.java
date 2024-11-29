package faang.school.projectservice.controller.managingTeamMembers;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.service.managingTeamService.managingTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/managing-team")
public class ManagingTeamController {

    private final managingTeamService ManagingTeamService;

    @PostMapping("/{projectId}")
    public ResponseEntity<TeamMemberDto> addTeamMember(
            @PathVariable Long projectId,
            @Valid @RequestBody TeamMemberDto teamMemberDto,
            @RequestParam Long teamMemberId) {
        return ResponseEntity.ok(ManagingTeamService.addTeamMember(projectId, teamMemberDto, teamMemberId));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<TeamMemberDto> updateTeamMember(
            @PathVariable Long projectId,
            @Valid @RequestBody TeamMemberDto teamMemberDto,
            @RequestParam Long teamMemberId,
            @RequestParam Long currentUserId) {
        return ResponseEntity.ok(ManagingTeamService.updateTeamMember(projectId, teamMemberDto, teamMemberId, currentUserId));
    }

    @DeleteMapping("/{projectId}/{teamMemberId}")
    public ResponseEntity<TeamMemberDto> deleteTeamMember(
            @PathVariable Long projectId,
            @PathVariable Long teamMemberId,
            @RequestParam Long currentUserId) {
        return ResponseEntity.ok(ManagingTeamService.deleteTeamMember(projectId, teamMemberId, currentUserId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TeamMemberDto>> getTeamMembersWithFilter(
            @PathVariable Long projectId,
            @Valid @RequestBody TeamMemberFilterDto teamMemberFilterDto) {
        return ResponseEntity.ok(ManagingTeamService.getTeamMemberWithFilter(projectId, teamMemberFilterDto));
    }

    @GetMapping("/{projectId}/all")
    public ResponseEntity<List<TeamMemberDto>> getAllTeamMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(ManagingTeamService.getAllMembers(projectId));
    }

    @GetMapping("/{projectId}/{teamMemberId}")
    public ResponseEntity<TeamMemberDto> getTeamMember(
            @PathVariable Long projectId,
            @PathVariable Long teamMemberId) {
        return ResponseEntity.ok(ManagingTeamService.getTeamMember(projectId, teamMemberId));
    }
}
