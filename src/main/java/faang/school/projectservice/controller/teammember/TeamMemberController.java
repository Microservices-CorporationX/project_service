package faang.school.projectservice.controller.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberUpdateDto;
import faang.school.projectservice.service.teammember.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "API for managing team members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @Operation(summary = "Add a new team member")
    @PostMapping()
    public ResponseEntity<TeamMemberDto> addTeamMember(
            @PathVariable @Positive Long projectId,
            @Valid @RequestBody TeamMemberDto teamMemberDto) {
        log.info("Adding new team member to project {}: {}", projectId, teamMemberDto);
        TeamMemberDto createdMember = teamMemberService.addMemberToTheTeam(projectId, teamMemberDto);
        log.info("Team member added successfully to project {}: {}", projectId, createdMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @Operation(summary = "Update an existing team member")
    @PutMapping()
    public ResponseEntity<TeamMemberDto> updateMember(
            @PathVariable @Positive Long projectId,
            @Valid @RequestBody TeamMemberUpdateDto teamMemberUpdateDto) {
        log.info("Updating team member with id {} in project {}: {}", teamMemberUpdateDto.getUpdateUserId(), projectId, teamMemberUpdateDto);
        TeamMemberDto updatedMember = teamMemberService.updateMemberInTheTeam(projectId, teamMemberUpdateDto);
        log.info("Team member updated successfully in project {}: {}", projectId, updatedMember);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "Delete a team member")
    @DeleteMapping("/{deleteUserId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable @Positive Long projectId,
            @PathVariable @Positive Long deleteUserId) {
        log.info("Deleting team member with id {} from project {}", deleteUserId, projectId);
        teamMemberService.deleteMemberFromTheTeam(projectId, deleteUserId);
        log.info("Team member deleted successfully from project {}", projectId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all team members by filter")
    @PostMapping("/filter")
    public ResponseEntity<List<TeamMemberDto>> getAllMembersByFilter(
            @PathVariable @Positive Long projectId,
            @RequestBody TeamMemberFilterDto teamMemberFilter) {
        log.info("Getting all team members with filter for project {}: {}", projectId, teamMemberFilter);
        List<TeamMemberDto> members = teamMemberService.getAllMembersWithFilter(projectId, teamMemberFilter);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get all team members from a project")
    @GetMapping()
    public ResponseEntity<List<TeamMemberDto>> getAllMembersFromTheProject(@PathVariable @Positive Long projectId) {
        log.info("Getting all team members for project: {}", projectId);
        List<TeamMemberDto> members = teamMemberService.getAllMembersFromTheProject(projectId);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get a team member by id")
    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDto> getMemberById(
            @PathVariable @Positive Long projectId,
            @PathVariable @Positive Long id) {
        log.info("Getting team member with id {} from project {}", id, projectId);
        TeamMemberDto member = teamMemberService.getMemberById(projectId, id);
        return ResponseEntity.ok(member);
    }
}
