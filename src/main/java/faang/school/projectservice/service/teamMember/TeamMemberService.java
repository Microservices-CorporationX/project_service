package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberCreateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.filter.members.TeamMemberFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.member.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final UserServiceClient userServiceClient;
    private final TeamMemberMapper teamMemberMapper;
    private final UserContext userContext;
    private final List<TeamMemberFilter> teamMemberFilters;
    private final TeamService teamService;

    @Transactional
    public TeamMemberDto addTeamMember(long teamId, TeamMemberCreateDto teamMemberCreateDto) {
        long userId = getUserId();
        checkUserExists(userId);
        long projectId = getTeam(teamId).getProject().getId();

        var user = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);

        if (!user.getRoles().contains(TeamRole.TEAMLEAD) &&
                !user.getRoles().contains(TeamRole.OWNER)) {
            throw new UnauthorizedAccessException("User doesn't have the right to add new participants");
        }

        if (!teamMemberJpaRepository.findByUserIdAndProjectId(
                teamMemberCreateDto.userId(), projectId).getRoles().isEmpty()) {
            log.error("Attempt to add user with ID {} to team {} failed: user is already a member of the project",
                    teamMemberCreateDto.userId(), teamId);
            throw new DataValidationException("This user already in team");
        }

        var teamMember = teamMemberMapper.toTeamMember(teamMemberCreateDto);
        teamMember.setId(teamMemberCreateDto.userId());
        teamMember.setRoles(teamMemberCreateDto.roles());

        teamMemberJpaRepository.save(teamMember);
        log.info("Successfully added team member: {}", teamMember);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    @Transactional
    public TeamMemberDto updateTeamMember(long teamId, long teamMemberId, TeamMemberUpdateDto teamMemberUpdateDto) {
        long userId = getUserId();
        checkUserExists(userId);
        long projectId = getTeam(teamId).getProject().getId();

        var user = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);

        if (!user.getRoles().contains(TeamRole.TEAMLEAD)) {
            throw new DataValidationException("Only teamleader can update member");
        }
        var teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(teamMemberId, projectId);

        if (!teamMember.getRoles().isEmpty()) {
            teamMember.setRoles(teamMemberUpdateDto.roles());
        }

        teamMemberJpaRepository.save(teamMember);
        log.info("Successfully updated team member: {}", teamMember);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    @Transactional
    public void deleteTeamMember(long teamMemberId, long teamId) {
        long userId = getUserId();
        var member = teamMemberJpaRepository.findByUserIdAndProjectId(userId, getTeam(teamId).getProject().getId());

        if (!member.getRoles().contains(TeamRole.OWNER)) {
            log.error("Unauthorized access: User with ID {} attempted to delete member {} without OWNER role", userId, teamMemberId);
            throw new UnauthorizedAccessException("Only project owner can delete members");
        }

        teamMemberJpaRepository.deleteById(teamMemberId);
        log.info("Successfully deleted team member with id: {}", teamMemberId);
    }

    @Transactional
    public List<TeamMemberDto> getTeamMembersByFilter(long teamId, TeamFilterDto filters) {
        Stream<TeamMember> teamMembers = teamMemberJpaRepository.findAll().stream()
                .filter(teamMember -> teamMember.getTeam().getId().equals(teamId));

        return teamMemberFilters.stream()
                .filter(teamMemberFilter -> teamMemberFilter.isApplicable(filters))
                .reduce(teamMembers,
                        (currentStream, teamMemberFilter) -> teamMemberFilter.apply(teamMembers, filters),
                        (s1, s2) -> s1)
                .map(teamMemberMapper::toTeamMemberDto)
                .toList();
    }

    @Transactional
    public Page<TeamMemberDto> getAllTeamMembers(Pageable pageable) {
        Page<TeamMember> teamMembers = teamMemberJpaRepository.findAll(pageable);
        return teamMembers.map(teamMemberMapper::toTeamMemberDto);
    }

    public TeamMemberDto getTeamMemberById(long teamMemberId) {
        var teamMember = teamMemberRepository.findById(teamMemberId);
        return teamMemberMapper.toTeamMemberDto(teamMember);
    }

    private void checkUserExists(long userId) {
        userServiceClient.getUser(userId);
    }

    private long getUserId() {
        return userContext.getUserId();
    }

    private Team getTeam(long teamId) {
        return teamService.getTeamById(teamId);
    }
}