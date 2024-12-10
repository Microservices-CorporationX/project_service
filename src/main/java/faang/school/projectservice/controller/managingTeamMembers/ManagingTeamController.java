package faang.school.projectservice.controller.managingTeamMembers;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.service.managingTeamService.ManagingTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/managing-team/projects")
public class ManagingTeamController {

    private final ManagingTeamService managingTeamService;

    @PostMapping("/{projectId}")
    public ResponseEntity<TeamMemberDto> addTeamMember(
            @PathVariable Long projectId,
            @Valid @RequestBody TeamMemberDto teamMemberDto,
            @RequestParam Long teamMemberId) {
        log.info("Request to add a team member with id: {}", teamMemberId);
        return ResponseEntity.ok(managingTeamService.addTeamMember(projectId, teamMemberDto, teamMemberId));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<TeamMemberDto> updateTeamMember(
            @PathVariable Long projectId,
            @Valid @RequestBody TeamMemberDto teamMemberDto,
            @RequestParam Long teamMemberId,
            @RequestParam Long currentUserId) {
        log.info("Request to update a team member with id: {}", teamMemberId);
        return ResponseEntity.ok(managingTeamService.updateTeamMember(projectId, teamMemberDto, teamMemberId, currentUserId));
    }

    @DeleteMapping("/{projectId}/{teamMemberId}/{currentUserId}")
    public ResponseEntity<TeamMemberDto> deleteTeamMember(
            @PathVariable Long projectId,
            @PathVariable Long teamMemberId,
            @PathVariable Long currentUserId) {
        log.info("Request to delete a team member with id: {}", teamMemberId);
        return ResponseEntity.ok(managingTeamService.deleteTeamMember(projectId, teamMemberId, currentUserId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TeamMemberDto>> getTeamMembersWithFilter(
            @PathVariable Long projectId,
            @Valid @RequestBody TeamMemberFilterDto teamMemberFilterDto) {
        log.info("Request to get team members with filter: {}", teamMemberFilterDto);
        return ResponseEntity.ok(managingTeamService.getTeamMemberWithFilter(projectId, teamMemberFilterDto));
    }

    @GetMapping("/{projectId}/all")
    public ResponseEntity<List<TeamMemberDto>> getAllTeamMembers(@PathVariable Long projectId) {
        log.info("Request to get all team members of the project with id: {}", projectId);
        return ResponseEntity.ok(managingTeamService.getAllMembers(projectId));
    }

    @GetMapping("/{projectId}/{teamMemberId}")
    public ResponseEntity<TeamMemberDto> getTeamMember(
            @PathVariable Long projectId,
            @PathVariable Long teamMemberId) {
        log.info("Request to get team member with id: {}", teamMemberId);
        return ResponseEntity.ok(managingTeamService.getTeamMember(projectId, teamMemberId));
    }
}
