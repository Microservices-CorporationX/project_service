package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamEvent;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.team.TeamMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.team.TeamEventPublisher;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.team.TeamValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Spy
    private TeamMapperImpl teamMapper;

    @Mock
    private TeamValidator teamValidator;

    @Mock
    private TeamEventPublisher teamEventPublisher;

    @InjectMocks
    private TeamService teamService;

    @Captor
    private ArgumentCaptor<Team> teamCaptor;

    @Captor
    private ArgumentCaptor<TeamEvent> teamEventCaptor;

    @Test
    void testSuccessfulCreateInternship() {
        TeamDto teamDto = prepareTeamDto();
        TeamMember firstMember = TeamMember.builder()
                .id(2L)
                .build();
        TeamMember secondMember = TeamMember.builder()
                .id(3L)
                .build();
        List<TeamMember> teamMembers = List.of(firstMember, secondMember);
        when(teamMemberRepository.findAllById(teamDto.getTeamMemberIds())).thenReturn(teamMembers);

        TeamDto teamDtoAfterSave = teamService.createTeam(teamDto);
        verify(teamRepository).save(teamCaptor.capture());
        Team teamToSave = teamCaptor.getValue();
        verify(teamEventPublisher).publish(teamEventCaptor.capture());
        TeamEvent event = teamEventCaptor.getValue();

        assertEquals(teamDto, teamDtoAfterSave);
        verify(teamRepository, times(1)).save(teamToSave);
        verify(teamEventPublisher).publish(event);
    }

    @Test
    void testGetAllInternships() {
        List<Team> teams = List.of(prepareTeam(), prepareTeam());
        List<TeamDto> expectedTeams = teamMapper.mapToDtoList(teams);
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
                .teamMemberIds(List.of(2L, 3L))
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
