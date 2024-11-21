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
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.team.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.TeamRole.TEAMLEAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {
    public static final long TEAM_MEMBER_ID = 1L;
    public static final long USER_ID = 123L;
    public static final long TEAM_ID = 2L;
    public static final long PROJECT_ID = 1L;
    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    TeamMemberJpaRepository teamMemberJpaRepository;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    TeamMemberMapper teamMemberMapper;

    @Mock
    TeamMemberFilter filterMock;

    @Mock
    UserContext userContext;

    @Mock
    TeamService teamService;

    @InjectMocks
    TeamMemberService teamMemberService;


    @Test
    void positiveAddTeamMember() {
        Project project = getProject();
        TeamMember user = getTeamMember(TEAMLEAD);
        Team team = getTeam(project);
        TeamMemberCreateDto teamMemberCreateDto = new TeamMemberCreateDto(2L, List.of(MANAGER));

        TeamMember newMember = TeamMember.builder()
                .roles(new ArrayList<>())
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(user);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(teamMemberCreateDto.userId(), PROJECT_ID))
                .thenReturn(newMember);

        when(teamMemberMapper.toTeamMember(teamMemberCreateDto)).thenReturn(newMember);

        when(teamMemberJpaRepository.save(newMember)).thenReturn(newMember);

        teamMemberService.addTeamMember(TEAM_ID, teamMemberCreateDto);

        verify(userServiceClient, times(1)).getUser(USER_ID);
        verify(teamService, times(1)).getTeamById(TEAM_ID);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(USER_ID, PROJECT_ID);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(teamMemberCreateDto.userId(), PROJECT_ID);
        verify(teamMemberJpaRepository, times(1)).save(newMember);
    }

    @Test
    @DisplayName("UnauthorizedAccessException")
    void negativeAddTeamMember() {
        Project project = getProject();
        TeamMember user = getTeamMember(DEVELOPER);
        Team team = getTeam(project);

        TeamMemberCreateDto teamMemberCreateDto = new TeamMemberCreateDto(2L, List.of(MANAGER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID,
                PROJECT_ID)).thenReturn(user);


        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () ->
                teamMemberService.addTeamMember(TEAM_ID, teamMemberCreateDto));

        assertEquals("User doesn't have the right to add new participants", exception.getMessage());
    }


    @Test
    @DisplayName("DataValidationException")
    void negativeAddTeamMemberDataValidationException() {
        Project project = getProject();
        TeamMember user = getTeamMember(TEAMLEAD);
        TeamMember existingMember = getTeamMember(MANAGER);
        Team team = getTeam(project);

        TeamMemberCreateDto teamMemberCreateDto = new TeamMemberCreateDto(2L, List.of(MANAGER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(user);

        when(teamMemberJpaRepository.findByUserIdAndProjectId(teamMemberCreateDto.userId(), PROJECT_ID))
                .thenReturn(existingMember);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                teamMemberService.addTeamMember(TEAM_ID, teamMemberCreateDto));

        assertEquals("This user already in team", exception.getMessage());
    }

    @Test
    void positiveUpdateTeamMember() {
        Project project = getProject();
        TeamMember user = getTeamMember(TEAMLEAD);
        TeamMember teamMember = getTeamMember(TEAMLEAD);
        Team team = getTeam(project);

        TeamMemberUpdateDto teamMemberUpdateDto = new TeamMemberUpdateDto(List.of(MANAGER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(user);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(TEAM_MEMBER_ID, PROJECT_ID)).thenReturn(teamMember);
        when(teamMemberJpaRepository.save(teamMember)).thenReturn(teamMember);

        teamMemberService.updateTeamMember(TEAM_ID, TEAM_MEMBER_ID, teamMemberUpdateDto);

        verify(userContext, times(1)).getUserId();
        verify(teamService, times(1)).getTeamById(TEAM_ID);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(USER_ID, PROJECT_ID);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(TEAM_MEMBER_ID, PROJECT_ID);
        verify(teamMemberJpaRepository, times(1)).save(teamMember);

        assertEquals(List.of(MANAGER), teamMember.getRoles());
    }

    @Test
    void positiveUpdateTeamMemberDataValidationException() {
        Project project = getProject();
        TeamMember user = getTeamMember(MANAGER);
        Team team = getTeam(project);

        TeamMemberUpdateDto teamMemberUpdateDto = new TeamMemberUpdateDto(List.of(MANAGER));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(user);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> teamMemberService.updateTeamMember(TEAM_ID, TEAM_MEMBER_ID,
                        teamMemberUpdateDto));

        assertEquals("Only teamleader can update member", exception.getMessage());
    }

    @Test
    @DisplayName("DeleteTeamMember_UnauthorizedAccessException")
    void positiveDeleteTeamMemberUnauthorizedAccessException() {
        Project project = getProject();
        TeamMember existingMember = getTeamMember(MANAGER);

        Team team = Team.builder()
                .id(TEAM_ID)
                .project(project)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(existingMember);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> teamMemberService.deleteTeamMember(TEAM_MEMBER_ID, TEAM_ID));

        verify(userContext, times(1)).getUserId();
        verify(teamService, times(1)).getTeamById(TEAM_ID);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(USER_ID, PROJECT_ID);

        assertEquals("Only project owner can delete members", exception.getMessage());
    }

    @Test
    void positiveDeleteTeamMember() {
        Project project = getProject();
        TeamMember existingMember = getTeamMember(OWNER);

        Team team = Team.builder()
                .id(TEAM_ID)
                .project(project)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamService.getTeamById(TEAM_ID)).thenReturn(team);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(existingMember);

        doNothing().when(teamMemberJpaRepository).deleteById(TEAM_MEMBER_ID);

        teamMemberService.deleteTeamMember(TEAM_MEMBER_ID, TEAM_ID);

        verify(userContext, times(1)).getUserId();
        verify(teamService, times(1)).getTeamById(TEAM_ID);
        verify(teamMemberJpaRepository, times(1)).findByUserIdAndProjectId(USER_ID, PROJECT_ID);
        verify(teamMemberJpaRepository, times(1)).deleteById(TEAM_MEMBER_ID);
    }

    @Test
    void getAllTeamMembers() {
        Pageable pageable = PageRequest.of(0, 5);

        TeamMember teamMember1 = TeamMember.builder()
                .id(1L)
                .roles(List.of(TEAMLEAD))
                .build();

        TeamMember teamMember2 = TeamMember.builder()
                .id(2L)
                .roles(List.of(MANAGER))
                .build();

        Page<TeamMember> teamMembersPage = new PageImpl<>(List.of(teamMember1, teamMember2));

        when(teamMemberJpaRepository.findAll(pageable)).thenReturn(teamMembersPage);

        TeamMemberDto teamMemberDto1 = TeamMemberDto.builder()
                .teamMemberId(1L)
                .roles(List.of(TEAMLEAD))
                .build();
        TeamMemberDto teamMemberDto2 = TeamMemberDto.builder()
                .teamMemberId(2L)
                .roles(List.of(MANAGER))
                .build();

        when(teamMemberMapper.toTeamMemberDto(teamMember1)).thenReturn(teamMemberDto1);
        when(teamMemberMapper.toTeamMemberDto(teamMember2)).thenReturn(teamMemberDto2);

        Page<TeamMemberDto> result = teamMemberService.getAllTeamMembers(pageable);

        verify(teamMemberJpaRepository, times(1)).findAll(pageable);
        verify(teamMemberMapper, times(1)).toTeamMemberDto(teamMember1);
        verify(teamMemberMapper, times(1)).toTeamMemberDto(teamMember2);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void positiveGetTeamMemberById() {
        TeamMember existingMember = getTeamMember(MANAGER);

        TeamMemberDto teamMemberDto = new TeamMemberDto(TEAM_MEMBER_ID, List.of(MANAGER));

        when(teamMemberRepository.findById(TEAM_MEMBER_ID)).thenReturn(existingMember);

        when(teamMemberMapper.toTeamMemberDto(existingMember)).thenReturn(teamMemberDto);

        TeamMemberDto result = teamMemberService.getTeamMemberById(TEAM_MEMBER_ID);

        verify(teamMemberRepository, times(1)).findById(TEAM_MEMBER_ID);
        verify(teamMemberMapper, times(1)).toTeamMemberDto(existingMember);
        assertEquals(teamMemberDto, result);
    }

    @Test
    void positiveGetTeamMembersByFilter() {
        TeamMemberDto teamMemberDto1 = new TeamMemberDto(TEAM_MEMBER_ID, List.of(TeamRole.TEAMLEAD));

        Team team = Team.builder().id(TEAM_ID).build();

        TeamMember teamMember1 =
                TeamMember.builder().id(TEAM_MEMBER_ID).team(team).roles(List.of(TeamRole.TEAMLEAD)).build();

        TeamMember teamMember2 = TeamMember.builder().id(2L).team(team).roles(List.of(TeamRole.MANAGER)).build();

        TeamFilterDto filterDto = new TeamFilterDto();
        filterDto.setTeamRole(TeamRole.TEAMLEAD);

        when(teamMemberJpaRepository.findAll()).thenReturn(List.of(teamMember1, teamMember2));

        when(filterMock.isApplicable(eq(filterDto))).thenReturn(true);
        when(filterMock.apply(any(), eq(filterDto))).thenReturn(Stream.of(teamMember1));

        when(teamMemberMapper.toTeamMemberDto(teamMember1)).thenReturn(teamMemberDto1);

        List<TeamMemberFilter> teamMemberFilters = List.of(filterMock);
        TeamMemberService teamMemberService = new TeamMemberService(teamMemberRepository, teamMemberJpaRepository,
                userServiceClient, teamMemberMapper, userContext,
                teamMemberFilters, teamService);

        List<TeamMemberDto> result = teamMemberService.getTeamMembersByFilter(TEAM_ID, filterDto);

        verify(teamMemberJpaRepository, times(1)).findAll();
        verify(teamMemberMapper, times(1)).toTeamMemberDto(teamMember1);

        assertEquals(1, result.size());
        assertTrue(result.contains(teamMemberDto1));
        assertFalse(result.contains(new TeamMemberDto(2L, List.of(TeamRole.MANAGER))));
    }


    private static Project getProject() {
        return Project.builder()
                .id(PROJECT_ID)
                .build();
    }

    private static TeamMember getTeamMember(TeamRole teamRole) {
        return TeamMember.builder()
                .roles(List.of(teamRole))
                .build();
    }

    private static Team getTeam(Project project) {
        return Team.builder()
                .project(project)
                .id(123L)
                .build();
    }
}