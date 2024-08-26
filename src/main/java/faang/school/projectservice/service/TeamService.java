package faang.school.projectservice.service;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.event.TeamEvent;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.TeamMessagePublisher;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final TeamMessagePublisher teamMessagePublisher;

    @Transactional(readOnly = true)
    public List<TeamMember> getAllTeamMember(Long TeamId) {
        Team team = teamRepository.findById(TeamId).orElse(null);
        assert team != null;
        return team.getTeamMembers();
    }

    @Transactional
    public Team createTeamEntity(TeamDto teamDto) {
        Team team = teamRepository.save(teamMapper.toEntity(teamDto));
        sendAchievementInfo(team);
        return team;
    }

    @Transactional
    public TeamDto createTeam(TeamDto teamDto) {
        return teamMapper.toDto(createTeamEntity(teamDto));
    }

    private void sendAchievementInfo(Team team) {
            TeamEvent event = TeamEvent.builder()
                    .teamId(team.getId())
                    .authorId(team.getProject().getOwnerId())
                    .projectId(team.getProject().getId())
                    .build();
            teamMessagePublisher.publish(event);
    }
}
