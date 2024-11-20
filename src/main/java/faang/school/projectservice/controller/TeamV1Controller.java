package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateTeamMemberDto;
import faang.school.projectservice.dto.ResponseTeamMemberDto;
import faang.school.projectservice.dto.UpdateTeamMemberDto;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    @PostMapping("/{teamId}/members/add")
    public ResponseTeamMemberDto addTeamMember(@RequestBody @Valid CreateTeamMemberDto teamMemberDto,
                                             @PathVariable @Positive Long teamId,
                                             HttpServletRequest request) {
        return teamService.addTeamMember(teamMemberDto, teamId, request);
    }

    @PutMapping("/{teamId}/members/update")
    public ResponseTeamMemberDto updateTeamMember(@RequestBody @Valid UpdateTeamMemberDto teamMemberDto,
                                                  @PathVariable @Positive Long teamId,
                                                  HttpServletRequest request) {
        return teamService.updateTeamMember(teamMemberDto, teamId, request);
    }

    @DeleteMapping("/{teamId}/members/delete/{memberId}")
    public void deleteTeamMember(@PathVariable @Positive Long teamId,
                                 @PathVariable @Positive Long memberId,
                                 HttpServletRequest request) {
        teamService.deleteTeamMember(teamId, memberId, request);
    }

    @GetMapping("/{teamId}/members")
    public List<ResponseTeamMemberDto> getMembersByTeamId(@PathVariable @Positive Long teamId) {
        return teamService.getTeamMembersByTeamId(teamId);
    }
}