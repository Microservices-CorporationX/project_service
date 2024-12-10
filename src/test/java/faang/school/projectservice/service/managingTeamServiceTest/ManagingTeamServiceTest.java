package faang.school.projectservice.service.managingTeamServiceTest;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.managingTeamMapper.ManagingTeamMapper;
import faang.school.projectservice.mapper.managingTeamMapper.ManagingTeamMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.managingTeamService.ManagingTeamService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagingTeamServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Mock
    private ManagingTeamMapperImpl teamMemberMapper;

    @InjectMocks
    private ManagingTeamService managingTeamService;

    @Test
    void addTeamMember_shouldAddMember_whenConditionsAreMet() {
        Long projectId = 1L;
        Long teamId = 2L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setUserId(3L);

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Team team = new Team();
        team.setId(teamId);
        project.setTeams(List.of(team));


        TeamMember teamMember = new TeamMember();
        teamMember.setId(2L);
        teamMember.setTeam(team);
        teamMember.setRoles(List.of(TeamRole.OWNER));

        team.setTeamMembers(List.of(teamMember));

        TeamMember savedMember = new TeamMember();
        savedMember.setId(4L);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(3L, projectId)).thenReturn(null);
        when(teamMemberMapper.toEntity(teamMemberDto)).thenReturn(teamMember);
        when(teamMemberJpaRepository.save(teamMember)).thenReturn(savedMember);
        when(teamMemberMapper.toDto(savedMember)).thenReturn(teamMemberDto);

        TeamMemberDto result = managingTeamService.addTeamMember(projectId, teamMemberDto, teamId);

        assertEquals(teamMemberDto, result);
        verify(teamMemberJpaRepository).save(teamMember);
    }

    @Test
    void addTeamMember_shouldThrowException_whenProjectIsCancelled() {
        Long projectId = 1L;
        Long teamId = 2L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        Project project = new Project();
        project.setId(projectId);
        project.setStatus(ProjectStatus.CANCELLED);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                managingTeamService.addTeamMember(projectId, teamMemberDto, teamId));
        assertEquals("Cannot add member to a cancelled or completed project", exception.getMessage());
    }

    @Test
    void addTeamMember_shouldThrowException_whenTeamNotFound() {
        Long projectId = 1L;
        Long teamId = 2L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setTeams(Collections.emptyList());

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                managingTeamService.addTeamMember(projectId, teamMemberDto, teamId));
        assertEquals("Team with given ID not found in project", exception.getMessage());
    }

    @Test
    void addTeamMember_shouldThrowException_whenUserAlreadyMember() {
        Long projectId = 1L;
        Long teamId = 2L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setUserId(3L);

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setId(projectId);

        Team team = new Team();
        team.setId(teamId);
        project.setTeams(List.of(team));

        TeamMember existingMember = new TeamMember();
        existingMember.setId(2L);
        existingMember.setUserId(3L);
        existingMember.setTeam(team);
        existingMember.setRoles(List.of(TeamRole.OWNER));

        team.setProject(project);
        team.setTeamMembers(List.of(existingMember));

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(3L, projectId)).thenReturn(existingMember);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                managingTeamService.addTeamMember(projectId, teamMemberDto, teamId));
        assertEquals("User is already a member of the project", exception.getMessage());
    }

    @Test
    void updateTeamMember_shouldUpdateMember_whenConditionsAreMet() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setUsername("New Nickname");
        teamMemberDto.setDescription("New Description");

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Team team = new Team();
        TeamMember existingMember = new TeamMember();
        existingMember.setId(teamMemberId);
        existingMember.setUserId(currentUserId);
        existingMember.setTeam(team);
        existingMember.setRoles(List.of(TeamRole.OWNER));

        TeamMember teamMemberToUpdate = new TeamMember();
        teamMemberToUpdate.setId(teamMemberId);
        teamMemberToUpdate.setName("New Nickname");
        teamMemberToUpdate.setDescription("New Description");

        TeamMember savedMember = new TeamMember();
        savedMember.setId(teamMemberId);
        savedMember.setName("New Nickname");
        savedMember.setDescription("New Description");



        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findById(teamMemberId)).thenReturn(java.util.Optional.of(existingMember));
        when(teamMemberMapper.toEntity(teamMemberDto)).thenReturn(teamMemberToUpdate);
        when(teamMemberJpaRepository.save(existingMember)).thenReturn(savedMember);
        when(teamMemberMapper.toDto(savedMember)).thenReturn(teamMemberDto);

        TeamMemberDto result = managingTeamService.updateTeamMember(projectId, teamMemberDto, teamMemberId, currentUserId);

        assertEquals("New Nickname", result.getUsername());
        assertEquals("New Description", result.getDescription());
        verify(teamMemberJpaRepository).save(existingMember);
    }

    @Test
    void updateTeamMember_shouldThrowException_whenProjectIsCancelled() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                managingTeamService.updateTeamMember(projectId, teamMemberDto, teamMemberId, currentUserId));
        assertEquals("Cannot add member to a cancelled or completed project", exception.getMessage());
    }

    @Test
    void updateTeamMember_shouldThrowException_whenTeamMemberNotFound() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findById(teamMemberId)).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                managingTeamService.updateTeamMember(projectId, teamMemberDto, teamMemberId, currentUserId));
        assertEquals("Team member with given ID not found in project", exception.getMessage());
    }

    @Test
    void updateTeamMember_shouldThrowException_whenCurrentUserNotOwner() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Team team = new Team();
        TeamMember existingMember = new TeamMember();
        existingMember.setId(teamMemberId);
        existingMember.setUserId(4L); // Different user
        existingMember.setTeam(team);
        existingMember.setRoles(List.of(TeamRole.DEVELOPER)); // Not an owner

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findById(teamMemberId)).thenReturn(java.util.Optional.of(existingMember));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                managingTeamService.updateTeamMember(projectId, teamMemberDto, teamMemberId, currentUserId));
        assertEquals("User is not part of the project", exception.getMessage());
    }

    @Test
    void deleteTeamMember_shouldDeleteMember_whenConditionsAreMet() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;

        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberId);
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        teamMember.setUserId(currentUserId);

        TeamMember currentUser = new TeamMember();
        currentUser.setUserId(currentUserId);
        currentUser.setRoles(List.of(TeamRole.OWNER));

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setId(projectId);

        Team team = new Team();
        team.setId(1L);
        team.setProject(project);
        team.setTeamMembers(List.of(teamMember, currentUser));

        teamMember.setTeam(team);
        currentUser.setTeam(team);

        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(teamMemberId);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findById(teamMemberId)).thenReturn(Optional.of(teamMember));
        when(teamMemberMapper.toDto(teamMember)).thenReturn(teamMemberDto);

        TeamMemberDto result = managingTeamService.deleteTeamMember(projectId, teamMemberId, currentUserId);

        assertEquals(teamMemberId, result.getId());
        verify(teamMemberJpaRepository).delete(teamMember);
    }


    @Test
    void deleteTeamMember_shouldThrowException_whenProjectIsCancelledOrCompleted() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;

        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                managingTeamService.deleteTeamMember(projectId, teamMemberId, currentUserId)
        );

        assertEquals("Cannot add member to a cancelled or completed project", exception.getMessage());
        Mockito.verifyNoInteractions(teamMemberJpaRepository);
    }

    @Test
    void deleteTeamMember_shouldThrowException_whenTeamMemberNotFound() {
        Long projectId = 1L;
        Long teamMemberId = 2L;
        Long currentUserId = 3L;

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findById(teamMemberId)).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                managingTeamService.deleteTeamMember(projectId, teamMemberId, currentUserId)
        );

        assertEquals("Team member with given ID not found in project", exception.getMessage());
    }

    @Test
    void shouldReturnAllTeamMembersForActiveProject() {
        Long projectId = 1L;
        Project activeProject = new Project();
        activeProject.setId(projectId);
        activeProject.setStatus(ProjectStatus.IN_PROGRESS);

        TeamMember teamMember1 = TeamMember.builder()
                .id(1L)
                .userId(100L)
                .name("User1")
                .build();

        TeamMember teamMember2 = TeamMember.builder()
                .id(2L)
                .userId(101L)
                .name("User2")
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(activeProject);
        when(teamMemberJpaRepository.findAllByProjectId(projectId)).thenReturn(List.of(teamMember1, teamMember2));
        when(teamMemberMapper.toDto(any(TeamMember.class))).thenAnswer(invocation -> {
            TeamMember member = invocation.getArgument(0);
            return TeamMemberDto.builder()
                    .id(member.getId())
                    .userId(member.getUserId())
                    .username(member.getName())
                    .build();
        });

        List<TeamMemberDto> result = managingTeamService.getAllMembers(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getUsername());
        assertEquals("User2", result.get(1).getUsername());
        verify(projectRepository).getProjectById(projectId);
        verify(teamMemberJpaRepository).findAllByProjectId(projectId);
        verify(teamMemberMapper, times(2)).toDto(any(TeamMember.class));
    }

    @Test
    void shouldThrowExceptionForCancelledProject() {
        Long projectId = 2L;
        Project cancelledProject = new Project();
        cancelledProject.setId(projectId);
        cancelledProject.setStatus(ProjectStatus.CANCELLED);

        when(projectRepository.getProjectById(projectId)).thenReturn(cancelledProject);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            managingTeamService.getAllMembers(projectId);
        });

        assertEquals("Cannot add member to a cancelled or completed project", exception.getMessage());
        verify(projectRepository).getProjectById(projectId);
        verifyNoInteractions(teamMemberJpaRepository, teamMemberMapper);
    }

    @Test
    void shouldReturnTeamMemberDtoWhenMemberExists() {
        Long projectId = 1L;
        Long teamMemberId = 2L;

        Project project = new Project();
        project.setId(projectId);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        Team team = Team.builder()
                .id(1L)
                .project(project)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .id(teamMemberId)
                .userId(100L)
                .name("User1")
                .description("A team member")
                .accessLevel(3)
                .team(team)
                .build();

        TeamMemberDto expectedDto = TeamMemberDto.builder()
                .id(teamMemberId)
                .userId(100L)
                .username("User1")
                .description("A team member")
                .accessLevel(3)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamMemberJpaRepository.findById(teamMemberId)).thenReturn(Optional.of(teamMember));
        when(teamMemberMapper.toDto(teamMember)).thenReturn(expectedDto);

        TeamMemberDto result = managingTeamService.getTeamMember(projectId, teamMemberId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(teamMemberJpaRepository).findById(teamMemberId);
        verify(teamMemberMapper).toDto(teamMember);
    }

    @Test
    void shouldThrowExceptionWhenFiltersNotInitialized() {
        TeamMemberFilterDto filters = new TeamMemberFilterDto();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            managingTeamService.getTeamMemberWithFilter(1L, null);
        });

        assertEquals("Team member filters are not initialized", exception.getMessage());
    }
}
