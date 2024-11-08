package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.filter.internship.InternshipTeamRoleFilter;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    private static final int MAX_INTERNSHIP_MONTHS_DURATION = 3;
    private static final List<Long> DEFAULT_INTERN_USER_IDS = List.of(1L, 2L, 3L, 4L);
    private static final Long DEFAULT_MENTOR_USER_ID = 8L;
    private static final Long DEFAULT_PROJECT_ID = 10L;
    private static final LocalDateTime DEFAULT_START_DATE = LocalDateTime.now().plusMonths(1);
    private static final LocalDateTime DEFAULT_END_DATE = LocalDateTime.now().plusMonths(1 + MAX_INTERNSHIP_MONTHS_DURATION);
    private static final Long DEFAULT_CREATOR_USER_ID = 9L;
    private static final String DEFAULT_INTERNSHIP_DESCRIPTION = "Some description";
    private static final String DEFAULT_INTERNSHIP_NAME = "Internship Spring 2025";
    private static final Long DEFAULT_INTERNSHIP_ID = 9L;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipDtoValidator validator;

    @Spy
    private InternshipMapperImpl internshipMapper;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private TeamService teamService;

    private InternshipService internshipService;

    private List<InternshipFilter> filters;

    @BeforeEach
    void setUp() {
        InternshipFilter statusFilter = Mockito.spy(InternshipStatusFilter.class);
        InternshipFilter roleFilter = Mockito.spy(InternshipTeamRoleFilter.class);
        filters = List.of(statusFilter, roleFilter);

        internshipService = new InternshipService(
                internshipRepository, validator, internshipMapper, teamMemberService, teamService, filters
        );
    }

    @Test
    void createInternshipValidTest() {
        Project project = new Project();
        project.setId(DEFAULT_PROJECT_ID);
        Team mentorTeam = new Team();
        mentorTeam.setProject(project);
        TeamMember mentor = new TeamMember();
        mentor.setUserId(DEFAULT_MENTOR_USER_ID);
        mentor.setTeam(mentorTeam);

        InternshipCreationDto creationDto = createDefaultCreationDto();

        when(validator.validateCreationDtoAndGetMentor(creationDto)).thenReturn(mentor);
        when(internshipRepository.save(any(Internship.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(teamMemberService.save(any(TeamMember.class))).thenAnswer(invocation -> {
            TeamMember teamMember = invocation.getArgument(0);
            TeamMember savedTeamMember = new TeamMember();
            savedTeamMember.setUserId(teamMember.getUserId());
            savedTeamMember.setRoles(List.of(TeamRole.INTERN));
            return savedTeamMember;
        });

        InternshipDto savedInternshipDto = assertDoesNotThrow(() -> internshipService.createInternship(creationDto));

        verify(internshipRepository, times(1)).save(any(Internship.class));
        assertEquals(DEFAULT_INTERN_USER_IDS, savedInternshipDto.getInternUserIds());
        assertEquals(DEFAULT_INTERNSHIP_NAME, savedInternshipDto.getName());
        assertEquals(DEFAULT_INTERNSHIP_DESCRIPTION, savedInternshipDto.getDescription());
        assertEquals(DEFAULT_MENTOR_USER_ID, savedInternshipDto.getMentorUserId());
        assertEquals(DEFAULT_CREATOR_USER_ID, savedInternshipDto.getCreatorUserId());
        assertEquals(DEFAULT_PROJECT_ID, savedInternshipDto.getProjectId());
        assertEquals(DEFAULT_START_DATE, savedInternshipDto.getStartDate());
        assertEquals(DEFAULT_END_DATE, savedInternshipDto.getEndDate());
        assertEquals(InternshipStatus.NOT_STARTED, savedInternshipDto.getStatus());
    }

    @Test
    void updateInternshipInternshipInProgressTest() {
        updateInternshipValidTest(false, 2, InternshipStatus.IN_PROGRESS);
    }

    @Test
    void updateInternshipInternshipAfterEndDateTest() {
        updateInternshipValidTest(true, 1, InternshipStatus.COMPLETED);
    }

    @Test
    void getFilteredInternshipsEmptyFilterTest() {
        getFilteredInternshipsValidTest(null, null, 6);
    }

    @Test
    void getFilteredInternshipsStatusFilterTest() {
        getFilteredInternshipsValidTest(InternshipStatus.COMPLETED, null, 3);
    }

    @Test
    void getFilteredInternshipsTeamRoleFilterTest() {
        getFilteredInternshipsValidTest(null, TeamRole.ANALYST, 2);
    }

    @Test
    void getFilteredInternshipsStatusAndTeamRoleFilterTest() {
        getFilteredInternshipsValidTest(InternshipStatus.COMPLETED, TeamRole.ANALYST, 1);
    }

    @Test
    void getAllInternshipsValidTest() {
        List<Internship> internships = prepareInternships();
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> internshipDtos = assertDoesNotThrow(() -> internshipService.getAllInternships());

        assertEquals(internships.size(), internshipDtos.size());
        verify(internshipRepository, times(1)).findAll();
        verify(internshipMapper, times(1)).toDto(internships);
    }

    @Test
    void getInternshipByIdValidTest() {
        Internship internship = createDefaultInternship();
        when(internshipRepository.findById(DEFAULT_INTERNSHIP_ID)).thenReturn(Optional.of(internship));

        assertDoesNotThrow(() -> internshipService.getInternshipById(DEFAULT_INTERNSHIP_ID));

        verify(internshipRepository, times(1)).findById(DEFAULT_INTERNSHIP_ID);
        verify(internshipMapper, times(1)).toDto(internship);
    }

    @Test
    void getInternshipByIdNotExistingInternshipTest() {
        when(internshipRepository.findById(DEFAULT_INTERNSHIP_ID)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> internshipService.getInternshipById(DEFAULT_INTERNSHIP_ID));

        verify(internshipRepository, times(1)).findById(DEFAULT_INTERNSHIP_ID);
        assertEquals("There is no internship with ID (%d) in the database!".formatted(DEFAULT_INTERNSHIP_ID), exception.getMessage());
    }

    private void updateInternshipValidTest(boolean isAfterEndDate, int expectedTeamSize, InternshipStatus internshipStatus) {
        Internship internship = createDefaultInternship();
        Team internTeam = internship.getInterns().get(0).getTeam();

        if (isAfterEndDate) {
            internship.setEndDate(LocalDateTime.now().minusDays(1));
        }
        InternshipUpdateDto updateDto = createDefaultUpdateDto();

        when(validator.validateUpdateDtoAndGetInternship(updateDto)).thenReturn(internship);

        InternshipUpdateRequestDto requestDto = assertDoesNotThrow(() -> internshipService.updateInternship(updateDto));

        verify(teamService, times(1)).save(internTeam);
        verify(internshipRepository, times(1)).save(internship);
        assertEquals(expectedTeamSize, internTeam.getTeamMembers().size());
        assertEquals(updateDto.getInternshipId(), requestDto.getId());
        assertEquals(List.of(DEFAULT_INTERN_USER_IDS.get(0)), requestDto.getIdsOfUsersWithCompletedTasks());
        assertEquals(updateDto.getInternNewTeamRole(), requestDto.getInternNewTeamRole());
        assertEquals(internshipStatus, requestDto.getInternshipStatus());
        assertTrue(internTeam.getTeamMembers().get(0).getRoles().contains(updateDto.getInternNewTeamRole()));
    }

    private void getFilteredInternshipsValidTest(InternshipStatus internshipStatus, TeamRole teamRole,
                                                 int expectedFilteredInternshipsNumbers) {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(internshipStatus)
                .teamRole(teamRole)
                .build();
        List<Internship> internships = prepareInternships();
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(expectedFilteredInternshipsNumbers, filteredInternshipDtos.size());
    }

    private List<Internship> prepareInternships() {
        return List.of(
                createInternship(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternship(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternship(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternship(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternship(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternship(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
    }

    private Internship createInternship(InternshipStatus internshipStatus, TeamRole mentorTeamRole) {
        Internship internship = createDefaultInternship();
        TeamMember mentor = new TeamMember();
        mentor.setRoles(List.of(mentorTeamRole));
        internship.setMentorId(mentor);
        internship.setMentorId(mentor);
        internship.setStatus(internshipStatus);
        internship.setInterns(Collections.emptyList());
        return internship;
    }

    private Internship createDefaultInternship() {
        Team internTeam = new Team();
        List<Task> tasks = createDefaultTasksAndSetTeam(internTeam);
        Project project = new Project();
        project.setId(DEFAULT_PROJECT_ID);
        project.setTasks(tasks);
        internTeam.setProject(project);

        Internship internship = new Internship();
        internship.setId(DEFAULT_INTERNSHIP_ID);
        internship.setProject(project);
        internship.setStatus(InternshipStatus.NOT_STARTED);
        internship.setStartDate(LocalDateTime.now().minusMonths(2));
        internship.setEndDate(LocalDateTime.now().plusMonths(2));
        internship.setInterns(internTeam.getTeamMembers());

        return internship;
    }

    private List<Task> createDefaultTasksAndSetTeam(Team team) {
        Long firstInternUserId = DEFAULT_INTERN_USER_IDS.get(0);
        Long secondInternUserId = DEFAULT_INTERN_USER_IDS.get(1);

        TeamMember firstIntern = createIntern(firstInternUserId);
        TeamMember secondIntern = createIntern(secondInternUserId);
        team.setTeamMembers(new ArrayList<>(List.of(firstIntern, secondIntern)));
        firstIntern.setTeam(team);
        secondIntern.setTeam(team);

        Task firstInternFirstTask = createTask(firstInternUserId, TaskStatus.DONE);
        Task firstInternSecondTask = createTask(firstInternUserId, TaskStatus.DONE);
        Task secondInternFirstTask = createTask(secondInternUserId, TaskStatus.IN_PROGRESS);
        Task secondInternSecondTask = createTask(secondInternUserId, TaskStatus.DONE);

        return List.of(firstInternFirstTask, firstInternSecondTask, secondInternFirstTask, secondInternSecondTask);
    }

    private TeamMember createIntern(Long internUserId) {
        TeamMember intern = new TeamMember();
        intern.setUserId(internUserId);
        intern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
        return intern;
    }

    private Task createTask(Long performerUserId, TaskStatus taskStatus) {
        Task task = new Task();
        task.setPerformerUserId(performerUserId);
        task.setStatus(taskStatus);
        return task;
    }

    private InternshipCreationDto createDefaultCreationDto() {
        return InternshipCreationDto.builder()
                .internUserIds(DEFAULT_INTERN_USER_IDS)
                .name(DEFAULT_INTERNSHIP_NAME)
                .description(DEFAULT_INTERNSHIP_DESCRIPTION)
                .mentorUserId(DEFAULT_MENTOR_USER_ID)
                .creatorUserId(DEFAULT_CREATOR_USER_ID)
                .projectId(DEFAULT_PROJECT_ID)
                .startDate(DEFAULT_START_DATE)
                .endDate(DEFAULT_END_DATE)
                .build();
    }

    private InternshipUpdateDto createDefaultUpdateDto() {
        return InternshipUpdateDto.builder()
                .internshipId(DEFAULT_INTERNSHIP_ID)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();
    }
}