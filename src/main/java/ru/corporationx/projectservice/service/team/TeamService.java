package ru.corporationx.projectservice.service.team;

import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.mapper.team.TeamMapper;
import ru.corporationx.projectservice.model.dto.team.TeamDto;
import ru.corporationx.projectservice.model.dto.team.TeamEvent;
import ru.corporationx.projectservice.model.entity.Team;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.publisher.team.TeamEventPublisher;
import ru.corporationx.projectservice.repository.TeamMemberRepository;
import ru.corporationx.projectservice.repository.TeamRepository;
import ru.corporationx.projectservice.validator.team.TeamValidator;
import ru.corporationx.projectservice.validator.teammember.TeamMemberValidator;
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
