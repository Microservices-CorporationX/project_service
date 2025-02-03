package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity<TeamMemberDto> addMember(@RequestBody TeamMemberDto teamMemberDto,
                                                   @RequestParam Long projectId,
                                                   @RequestParam Long requesterId) {
        return ResponseEntity.ok(teamMemberService.addMember(teamMemberDto, projectId, requesterId));
    }

    @PutMapping("/update")
    public ResponseEntity<TeamMemberDto> updateMember(@RequestBody TeamMemberDto teamMemberDto,
                                                      @RequestParam Long requesterId,
                                                      @RequestParam Long projectId) {
        return ResponseEntity.ok(teamMemberService.updateMember(teamMemberDto, requesterId, projectId));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId,
                                             @RequestParam Long requesterId,
                                             @RequestParam Long projectId) {
        teamMemberService.removeMember(memberId, requesterId, projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TeamMemberDto>> getProjectMembers(@PathVariable Long projectId,
                                                                 @RequestParam(required = false) String role,
                                                                 @RequestParam(required = false) String name) {
        return ResponseEntity.ok(teamMemberService.getProjectMembers(projectId, role, name));
    }

    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getAllMembers() {
        return ResponseEntity.ok(teamMemberService.getAllMembers());
    }

    @GetMapping("/{memberId}/{projectId}")
    public ResponseEntity<TeamMemberDto> getMemberById(@PathVariable Long memberId,
                                                       @PathVariable Long projectId) {
        return ResponseEntity.ok(teamMemberService.getMemberById(memberId, projectId));
    }
}
