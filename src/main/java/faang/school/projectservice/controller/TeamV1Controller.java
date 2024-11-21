package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateTeamMemberDto;
import faang.school.projectservice.dto.ResponseTeamMemberDto;
import faang.school.projectservice.dto.UpdateTeamMemberDto;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@RestController
public class TeamV1Controller {
    private final TeamMemberService teamService;

    @PostMapping("/{teamId}/members")
    public ResponseTeamMemberDto addTeamMember(@RequestBody @Valid CreateTeamMemberDto teamMemberDto,
                                             @PathVariable @Positive @NotNull Long teamId) {
        return teamService.addTeamMember(teamMemberDto, teamId);
    }

    @PutMapping("/{teamId}/members/{memberId}")
    public ResponseTeamMemberDto updateTeamMember(@RequestBody @Valid UpdateTeamMemberDto teamMemberDto,
                                                  @PathVariable @Positive @NotNull Long teamId,
                                                  @PathVariable @Positive @NotNull Long memberId) {
        return teamService.updateTeamMember(teamMemberDto, teamId, memberId);
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public void deleteTeamMember(@PathVariable @Positive @NotNull Long teamId,
                                 @PathVariable @Positive @NotNull Long memberId) {
        teamService.deleteTeamMember(teamId, memberId);
    }

    @GetMapping("/{teamId}/members")
    public List<ResponseTeamMemberDto> getMembersByTeamId(@PathVariable @Positive @NotNull Long teamId) {
        return teamService.getTeamMembersByTeamId(teamId);
    }
}