package faang.school.projectservice.controller.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberDeleteDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberUpdateDto;
import faang.school.projectservice.model.TeamMember;
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
@RequestMapping("/team-members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "API for managing team members")
public class TeamMemberController {

    private static final String MEMBER_PATH = "/member/{id}";

    private final TeamMemberService teamMemberService;

    @Operation(summary = "Add a new team member")
    @PostMapping()
    public ResponseEntity<TeamMemberDto> addMemberToTheTeam(@Valid @RequestBody TeamMemberDto teamMemberDto) {
        log.info("Adding new team member: {}", teamMemberDto);
        TeamMemberDto createdMember = teamMemberService.addMemberToTheTeam(teamMemberDto);
        log.info("Team member added successfully: {}", createdMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @Operation(summary = "Update an existing team member")
    @PutMapping()
    public ResponseEntity<TeamMemberDto> updateMemberInTheTeam(@Valid @RequestBody TeamMemberUpdateDto teamMemberUpdateDto) {
        log.info("Updating team member with id {}: {}", teamMemberUpdateDto.getUpdateUserId(), teamMemberUpdateDto);
        TeamMemberDto updatedMember = teamMemberService.updateMemberInTheTeam(teamMemberUpdateDto);
        log.info("Team member updated successfully: {}", updatedMember);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "Delete a team member")
    @DeleteMapping()
    public ResponseEntity<Void> deleteMemberFromTheTeam(@RequestBody @Valid TeamMemberDeleteDto teamMemberDeleteDto) {
        log.info("Deleting team member with id: {}", teamMemberDeleteDto);
        teamMemberService.deleteMemberFromTheTeam(teamMemberDeleteDto);
        log.info("Team member deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all team members by filter")
    @GetMapping("/filter")
    public ResponseEntity<List<TeamMemberDto>> getAllMembersByFilter(@RequestBody TeamMemberFilterDto teamMemberFilter) {
        log.info("Getting all team members with filter: {}", teamMemberFilter);
        List<TeamMemberDto> members = teamMemberService.getAllMembersWithFilter(teamMemberFilter);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get all team members")
    @GetMapping()
    public ResponseEntity<List<TeamMemberDto>> getAllMembersFromTheProject(@PathVariable @Positive Long projectId) {
        log.info("Getting all team members");
        List<TeamMemberDto> members = teamMemberService.getAllMembersFromTheProject(projectId);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get a team member by id")
    @GetMapping(MEMBER_PATH)
    public ResponseEntity<TeamMemberDto> getMemberById(@PathVariable @Positive Long id) {
        log.info("Getting team member with id: {}", id);
        TeamMemberDto member = teamMemberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }
}
