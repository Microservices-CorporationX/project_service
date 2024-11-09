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

    @BeforeEach
    void setUp() {
        InternshipFilter statusFilter = Mockito.spy(InternshipStatusFilter.class);
        InternshipFilter roleFilter = Mockito.spy(InternshipTeamRoleFilter.class);
        List<InternshipFilter> filters = List.of(statusFilter, roleFilter);

        internshipService = new InternshipService(
                internshipRepository, validator, internshipMapper, teamMemberService, teamService, filters
        );
    }

    @Test
    void createInternshipValidTest() {
        InternshipStatus expectedInternshipStatus = InternshipStatus.NOT_STARTED;

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .name("Internship Spring 2025")
                .description("Some description")
                .mentorUserId(8L)
                .creatorUserId(9L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1 + MAX_INTERNSHIP_MONTHS_DURATION))
                .build();

        Project project = new Project();
        project.setId(10L);

        Team mentorTeam = new Team();
        mentorTeam.setProject(project);

        TeamMember mentor = new TeamMember();
        mentor.setUserId(8L);
        mentor.setTeam(mentorTeam);

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
        assertEquals(creationDto.getInternUserIds(), savedInternshipDto.getInternUserIds());
        assertEquals(creationDto.getName(), savedInternshipDto.getName());
        assertEquals(creationDto.getDescription(), savedInternshipDto.getDescription());
        assertEquals(creationDto.getMentorUserId(), savedInternshipDto.getMentorUserId());
        assertEquals(creationDto.getCreatorUserId(), savedInternshipDto.getCreatorUserId());
        assertEquals(creationDto.getProjectId(), savedInternshipDto.getProjectId());
        assertEquals(creationDto.getStartDate(), savedInternshipDto.getStartDate());
        assertEquals(creationDto.getEndDate(), savedInternshipDto.getEndDate());
        assertEquals(expectedInternshipStatus, savedInternshipDto.getStatus());
    }

    @Test
    void updateInternshipInternshipInProgressTest() {
        updateInternshipValidTest(false);
    }

    @Test
    void updateInternshipInternshipCompletedTest() {
        updateInternshipValidTest(true);
    }

    @Test
    void getFilteredInternshipsEmptyFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(null)
                .teamRole(null)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(6, filteredInternshipDtos.size());
    }

    @Test
    void getFilteredInternshipsStatusFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(InternshipStatus.COMPLETED)
                .teamRole(null)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(3, filteredInternshipDtos.size());
    }

    @Test
    void getFilteredInternshipsTeamRoleFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(null)
                .teamRole(TeamRole.ANALYST)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(2, filteredInternshipDtos.size());
    }

    @Test
    void getFilteredInternshipsStatusAndTeamRoleFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(InternshipStatus.COMPLETED)
                .teamRole(TeamRole.ANALYST)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(1, filteredInternshipDtos.size());
    }

    @Test
    void getAllInternshipsValidTest() {
        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> internshipDtos = assertDoesNotThrow(() -> internshipService.getAllInternships());

        assertEquals(internships.size(), internshipDtos.size());
        verify(internshipRepository, times(1)).findAll();
        verify(internshipMapper, times(1)).toDto(internships);
    }

    @Test
    void getInternshipByIdValidTest() {
        Long internshipId = 5L;
        Internship internship = new Internship();
        internship.setInterns(Collections.emptyList());
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        assertDoesNotThrow(() -> internshipService.getInternshipById(internshipId));

        verify(internshipRepository, times(1)).findById(internshipId);
        verify(internshipMapper, times(1)).toDto(internship);
    }

    @Test
    void getInternshipByIdNotExistingInternshipTest() {
        Long internshipId = 5L;
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> internshipService.getInternshipById(internshipId));

        verify(internshipRepository, times(1)).findById(internshipId);
        assertEquals("There is no internship with ID (%d) in the database!".formatted(internshipId), exception.getMessage());
    }

    private void updateInternshipValidTest(boolean isAfterEndDate) {
        Long firstInternUserId = 1L;
        TeamMember firstIntern = new TeamMember();
        firstIntern.setUserId(firstInternUserId);
        firstIntern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));

        Long secondInternUserId = 2L;
        TeamMember secondIntern = new TeamMember();
        secondIntern.setUserId(secondInternUserId);
        secondIntern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));

        List<Task> tasks = List.of(
                Task.builder().performerUserId(firstInternUserId).status(TaskStatus.DONE).build(),
                Task.builder().performerUserId(firstInternUserId).status(TaskStatus.DONE).build(),
                Task.builder().performerUserId(secondInternUserId).status(TaskStatus.IN_PROGRESS).build(),
                Task.builder().performerUserId(secondInternUserId).status(TaskStatus.DONE).build()
        );

        InternshipStatus expectedInternshipStatus = isAfterEndDate ? InternshipStatus.COMPLETED : InternshipStatus.IN_PROGRESS;
        int expectedTeamSize = isAfterEndDate ? 1 : 2;
        List<Long> idsOfUsersWithCompletedTasks = List.of(firstInternUserId);

        long projectId = 9L;
        Project project = new Project();
        project.setId(projectId);
        project.setTasks(tasks);

        Team internTeam = new Team();
        internTeam.setTeamMembers(new ArrayList<>(List.of(firstIntern, secondIntern)));
        firstIntern.setTeam(internTeam);
        secondIntern.setTeam(internTeam);
        internTeam.setProject(project);

        Internship internship = new Internship();
        internship.setId(projectId);
        internship.setProject(project);
        internship.setStatus(InternshipStatus.NOT_STARTED);
        internship.setStartDate(LocalDateTime.now().minusMonths(2));
        internship.setInterns(internTeam.getTeamMembers());
        internship.setEndDate(isAfterEndDate ? LocalDateTime.now().minusDays(1) : LocalDateTime.now().plusDays(4));

        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(projectId)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();

        when(validator.validateUpdateDtoAndGetInternship(updateDto)).thenReturn(internship);

        InternshipUpdateRequestDto requestDto = assertDoesNotThrow(() -> internshipService.updateInternship(updateDto));

        verify(teamService, times(1)).save(internTeam);
        verify(internshipRepository, times(1)).save(internship);
        assertEquals(projectId, requestDto.getId());
        assertEquals(updateDto.getInternshipId(), requestDto.getId());
        assertEquals(updateDto.getInternNewTeamRole(), requestDto.getInternNewTeamRole());
        assertTrue(firstIntern.getRoles().contains(updateDto.getInternNewTeamRole()));
        assertEquals(idsOfUsersWithCompletedTasks, requestDto.getIdsOfUsersWithCompletedTasks());
        assertEquals(expectedTeamSize, internTeam.getTeamMembers().size());
        assertEquals(expectedInternshipStatus, requestDto.getInternshipStatus());
    }

    private Internship createInternshipWithStatusAndMentorRole(InternshipStatus internshipStatus, TeamRole mentorTeamRole) {
        TeamMember mentor = new TeamMember();
        mentor.setRoles(List.of(mentorTeamRole));

        Internship internship = new Internship();
        internship.setMentorId(mentor);
        internship.setStatus(internshipStatus);
        internship.setInterns(Collections.emptyList());
        return internship;
    }
}