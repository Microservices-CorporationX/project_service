package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.dto.internship.InternshipReadDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.StatusFilter;
import faang.school.projectservice.mapper.internship.InternshipCreateMapperImpl;
import faang.school.projectservice.mapper.internship.InternshipReadMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team_member.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    private static final Long INTERNSHIP_ID = 1L;
    private static final Long PROJECT_ID = 1L;
    private static final Long MENTOR_ID = 1L;
    private static final Long INTERN_ID = 2L;
    private static final TeamRole ROLE = TeamRole.DEVELOPER;
    private static final List<Long> INTERNS_IDS = List.of(INTERN_ID);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2025, 1, 1, 12, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2025, 4, 1, 12, 0);
    private static final InternshipStatus INTERNSHIP_STATUS = InternshipStatus.IN_PROGRESS;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private InternshipRepository internshipRepository;

    @Spy
    private InternshipCreateMapperImpl internshipCreateMapper;

    @Spy
    private InternshipReadMapperImpl internshipReadMapper;

    @Mock
    private InternshipValidator internshipValidator;

    @Mock
    private StatusFilter statusFilter;

//    @Mock
//    private RoleFilter roleFilter;

    private InternshipService internshipService;

    private List<InternshipFilter> filters;

    @BeforeEach
    void setUp() {
        filters = List.of(statusFilter);
        internshipService = new InternshipService(projectService,
                teamMemberService,
                teamMemberRepository,
                internshipRepository,
                internshipCreateMapper,
                internshipReadMapper,
                internshipValidator,
                filters);
    }

    @Test
    void testInternshipCreate() {
        InternshipCreateDto internshipDto = InternshipCreateDto.builder()
                .projectId(PROJECT_ID)
                .mentorId(MENTOR_ID)
                .role(ROLE)
                .internsIds(INTERNS_IDS)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .status(INTERNSHIP_STATUS)
                .build();

        Internship internship = internshipCreateMapper.toEntity(internshipDto);
        Project project = Project.builder()
                .id(PROJECT_ID)
                .build();
        TeamMember mentor = TeamMember.builder()
                .id(MENTOR_ID)
                .build();
        List<TeamMember> interns = List.of(TeamMember.builder()
                .id(INTERN_ID)
                .build());

        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(interns);

        Mockito.when(projectService.findById(PROJECT_ID)).thenReturn(project);
        Mockito.when(teamMemberService.findById(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(teamMemberRepository.findAllById(INTERNS_IDS)).thenReturn(interns);

        Internship awaitedInternship = Internship.builder()
                .project(project)
                .mentorId(mentor)
                .interns(interns)
                .build();
        InternshipReadDto awaitedInternshipDto = internshipReadMapper.toDto(awaitedInternship);

        Mockito.when(internshipRepository.save(internship)).thenReturn(awaitedInternship);

        InternshipReadDto result = internshipService.createInternship(internshipDto);
        Mockito.verify(projectService).findById(PROJECT_ID);
        Mockito.verify(teamMemberService).findById(MENTOR_ID);
        Mockito.verify(teamMemberRepository).findAllById(INTERNS_IDS);
        assertEquals(awaitedInternshipDto.getProjectId(), result.getProjectId());
        assertEquals(awaitedInternshipDto.getMentorId(), result.getMentorId());
        assertEquals(awaitedInternshipDto.getInternsIds(), result.getInternsIds());
    }

    @Test
    void testInternshipUpdateIfAllInternsCompletedInternship() {
        InternshipEditDto internshipDto = InternshipEditDto.builder()
                .id(INTERNSHIP_ID)
                .projectId(PROJECT_ID)
                .mentorId(MENTOR_ID)
                .role(ROLE)
                .internsIds(INTERNS_IDS)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .status(INTERNSHIP_STATUS)
                .build();

        Internship internship = Internship.builder()
                .id(INTERNSHIP_ID)
                .build();

        TeamMember intern = TeamMember.builder()
                .id(INTERN_ID)
                .roles(new ArrayList<>())
                .build();

        Mockito.when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        Mockito.when(internshipValidator.validateInternCompletedInternship(internshipDto, INTERN_ID)).thenReturn(true);
        Mockito.when(teamMemberService.findById(INTERN_ID)).thenReturn(intern);

        intern.getRoles().add(ROLE);
        internship.setInterns(List.of(intern));

        Internship expectedInternship = Internship.builder()
                .id(INTERNSHIP_ID)
                .interns(List.of(intern))
                .build();
        InternshipReadDto expectedInternshipDto = internshipReadMapper.toDto(expectedInternship);

        Mockito.when(internshipRepository.save(internship)).thenReturn(expectedInternship);

        InternshipReadDto result = internshipService.updateInternship(internshipDto);
        Mockito.verify(internshipRepository).findById(INTERNSHIP_ID);
        Mockito.verify(internshipValidator).validateInternCompletedInternship(internshipDto, INTERN_ID);
        Mockito.verify(teamMemberService).findById(INTERN_ID);
        assertEquals(expectedInternshipDto.getInternsIds(), result.getInternsIds());
    }
}
