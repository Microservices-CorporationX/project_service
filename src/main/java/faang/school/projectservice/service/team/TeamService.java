package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamEvent;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.team.TeamEventPublisher;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.team.TeamValidator;
import faang.school.projectservice.validator.teammember.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMapper teamMapper;
    private final TeamValidator teamValidator;
    private final TeamMemberValidator teamMemberValidator;
    private final TeamEventPublisher teamEventPublisher;

    @Transactional
    public TeamDto createTeam(TeamDto teamDto) {
        teamValidator.validateTeam(teamDto);
        teamValidator.validateAuthor(teamDto.getAuthorId());
        teamMemberValidator.validateMembers(teamDto.getTeamMembers());

        Team team = teamMapper.toEntity(teamDto);
        List<TeamMember> teamMembers = team.getTeamMembers();
        team.setTeamMembers(teamMembers);

        teamRepository.save(team);
        teamMembers.forEach(member -> member.setTeam(team));
        teamMemberRepository.saveAll(teamMembers);
        log.info("The team with ID {} was created.", team.getId());

        publishTeamCreationEvent(team);

        return teamMapper.toDto(team);
    }

    public List<TeamDto> getTeams() {
        List<Team> allTeams = teamRepository.findAll();
        log.info("The request for all teams was successful");
        return teamMapper.toDtoList(allTeams);
    }

    public TeamDto getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new DataValidationException(String.format("Team doesn't exist by id: %s", teamId)));
        log.info("The request for a team by ID {} was successful", teamId);
        return teamMapper.toDto(team);
    }

    private void publishTeamCreationEvent(Team team) {
        teamEventPublisher.publish(new TeamEvent(
                team.getId(),
                team.getAuthorId(),
                team.getProject().getId(),
                LocalDateTime.now()));
    }
}
