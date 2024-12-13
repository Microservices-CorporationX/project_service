package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamEvent;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.team.TeamEventPublisher;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.team.TeamValidator;
import faang.school.projectservice.validator.teammember.TeamMemberValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMemberMapper teamMemberMapper;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private TeamValidator teamValidator;

    @Mock
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private TeamEventPublisher teamEventPublisher;

    @InjectMocks
    private TeamService teamService;

    @Test
    void testCreateTeam() {
        TeamDto teamDto = prepareTeamDto();
        Team team = Team.builder()
                .id(1L)
                .authorId(2L)
                .project(Project.builder().id(3L).build())
                .teamMembers(List.of(new TeamMember()))
                .build();
        List<TeamMember> teamMembers = List.of(new TeamMember(), new TeamMember());

        when(teamMapper.toEntity(teamDto)).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(teamDto);

        TeamDto createdTeamDto = teamService.createTeam(teamDto);

        verify(teamValidator).validateTeam(teamDto);
        verify(teamValidator).validateAuthor(teamDto.getAuthorId());
        verify(teamMemberValidator).validateMembers(teamDto.getTeamMembers());
        verify(teamMapper).toEntity(teamDto);
        verify(teamRepository).save(team);
        verify(teamEventPublisher).publish(any());

        assertEquals(teamDto, createdTeamDto);
    }

    @Test
    void testGetAllTeams() {
        List<Team> teams = List.of(prepareTeam(), prepareTeam());
        List<TeamDto> expectedTeams = teamMapper.toDtoList(teams);
        when(teamRepository.findAll()).thenReturn(teams);

        List<TeamDto> actualTeams = teamService.getTeams();

        assertEquals(expectedTeams, actualTeams);
    }

    @Test
    void testGetTeamIfIdDoesNotExist() {
        Team team = prepareTeam();
        when(teamRepository.findById(team.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> teamService.getTeam(team.getId()));

        assertEquals("Team doesn't exist by id: 1", exception.getMessage());
    }

    @Test
    void testGetTeamById() {
        Team team = prepareTeam();
        TeamDto expectedDto = teamMapper.toDto(team);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        TeamDto actualDto = teamService.getTeam(team.getId());

        assertEquals(expectedDto, actualDto);
    }

    private TeamDto prepareTeamDto() {
        return TeamDto.builder()
                .id(1L)
                .authorId(5L)
                .teamMembers(List.of(
                        TeamMemberDto.builder().userId(2L).build(),
                        TeamMemberDto.builder().userId(3L).build()))
                .projectId(4L)
                .build();
    }

    private Team prepareTeam() {
        TeamMember firstMember = TeamMember.builder()
                .id(1L)
                .build();
        TeamMember secondMember = TeamMember.builder()
                .id(2L)
                .build();
        List<TeamMember> teamMembers = List.of(firstMember, secondMember);
        return Team.builder()
                .id(1L)
                .teamMembers(teamMembers)
                .project(new Project())
                .build();
    }
}
