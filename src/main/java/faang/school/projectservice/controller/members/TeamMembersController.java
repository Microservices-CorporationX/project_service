package faang.school.projectservice.controller.members;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberCreateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberUpdateDto;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/team_members")
@RequiredArgsConstructor
public class TeamMembersController {

    private final TeamMemberService teamMemberService;

    @PostMapping("/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamMemberDto addTeamMember(@Valid @Positive @PathVariable long teamId,
                                       @Valid @RequestBody TeamMemberCreateDto teamMemberCreateDto) {
        return teamMemberService.addTeamMember(teamId, teamMemberCreateDto);
    }

    @PutMapping("/{teamId}/{teamMemberId}")
    @ResponseStatus(HttpStatus.OK)
    public TeamMemberDto updateTeamMember(@Valid @Positive @PathVariable long teamId,
                                          @Valid @Positive @PathVariable long teamMemberId,
                                          @Valid @RequestBody TeamMemberUpdateDto teamMemberUpdateDto) {
        return teamMemberService.updateTeamMember(teamId, teamMemberId, teamMemberUpdateDto);
    }

    @DeleteMapping("/{teamMemberId}/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeamMember(@Valid @Positive @PathVariable long teamMemberId,
                                 @Valid @Positive @PathVariable long teamId) {
        teamMemberService.deleteTeamMember(teamMemberId, teamId);
    }

    @GetMapping("/{teamId}/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<TeamMemberDto> getTeamMembersByFilter(@Valid @Positive @PathVariable long teamId,
                                                      @Valid @RequestBody TeamFilterDto filters) {
        return teamMemberService.getTeamMembersByFilter(teamId, filters);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Page<TeamMemberDto> getAllTeamMembers(@PageableDefault Pageable pageable) {
        return teamMemberService.getAllTeamMembers(pageable);
    }

    @GetMapping("/{teamMemberId}")
    @ResponseStatus(HttpStatus.OK)
    public TeamMemberDto getTeamMemberById(@Valid @Positive @PathVariable long teamMemberId) {
        return teamMemberService.getTeamMemberById(teamMemberId);
    }
}
