package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internShip.InternShipCreatedDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectServiceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectServiceValidator projectServiceValidator;

    @Test
    public void testGetProjectTeamMembersIdsWithMentorPresent() {
        InternShipCreatedDto internShipCreatedDto = mock(InternShipCreatedDto.class);
        Project project = mock(Project.class);
        Team team = mock(Team.class);
        TeamMember teamMember = mock(TeamMember.class);
        TeamMember mentor = mock(TeamMember.class);

        when(internShipCreatedDto.getProjectId()).thenReturn(1L);
        when(internShipCreatedDto.getMentorId()).thenReturn(mentor);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        when(project.getTeams()).thenReturn(List.of(team));
        when(team.getTeamMembers()).thenReturn(List.of(teamMember));
        when(teamMember.getId()).thenReturn(2L);
        when(mentor.getId()).thenReturn(2L);

        when(projectServiceValidator.isMentorPresent(List.of(2L), 2L)).thenReturn(true);

        assertDoesNotThrow(() -> projectService.getProjectTeamMembersIds(internShipCreatedDto));

        verify(projectServiceValidator, times(1)).isMentorPresent(List.of(2L), 2L);
    }

    @Test
    public void testGetProjectTeamMembersIdsWithMentorAbsent() {
        InternShipCreatedDto internShipCreatedDto = mock(InternShipCreatedDto.class);
        Project project = mock(Project.class);
        Team team = mock(Team.class);
        TeamMember teamMember = mock(TeamMember.class);
        TeamMember mentor = mock(TeamMember.class);

        when(internShipCreatedDto.getProjectId()).thenReturn(1L);
        when(internShipCreatedDto.getMentorId()).thenReturn(mentor);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        when(project.getTeams()).thenReturn(List.of(team));
        when(team.getTeamMembers()).thenReturn(List.of(teamMember));
        when(teamMember.getId()).thenReturn(2L);
        when(mentor.getId()).thenReturn(3L);

        when(projectServiceValidator.isMentorPresent(List.of(2L), 3L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.getProjectTeamMembersIds(internShipCreatedDto));
        assertEquals("Mentor is not present in project team", exception.getMessage());
    }

    @Test
    public void testGetProjectTeamMembersIdsWithEmptyTeams() {
        InternShipCreatedDto internShipCreatedDto = mock(InternShipCreatedDto.class);
        Project project = mock(Project.class);
        TeamMember mentor = mock(TeamMember.class);

        when(internShipCreatedDto.getProjectId()).thenReturn(1L);
        when(internShipCreatedDto.getMentorId()).thenReturn(mentor);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(project.getTeams()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.getProjectTeamMembersIds(internShipCreatedDto);
        });
        assertEquals("Mentor is not present in project team", exception.getMessage());
    }

    @Test
    public void testGetProjectTeamMembersIdsWithEmptyTeamMembers() {
        InternShipCreatedDto internShipCreatedDto = mock(InternShipCreatedDto.class);
        Project project = mock(Project.class);
        Team team = mock(Team.class);
        TeamMember mentor = mock(TeamMember.class);

        when(internShipCreatedDto.getProjectId()).thenReturn(1L);
        when(internShipCreatedDto.getMentorId()).thenReturn(mentor);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(project.getTeams()).thenReturn(List.of(team));
        when(team.getTeamMembers()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.getProjectTeamMembersIds(internShipCreatedDto);
        });

        assertEquals("Mentor is not present in project team", exception.getMessage());
    }
}
