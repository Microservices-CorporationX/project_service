package faang.school.projectservice.service.internship;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipStatusDto;
import faang.school.projectservice.dto.internship.RoleDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectStatusDto;
import faang.school.projectservice.dto.project.ProjectVisibilityDto;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.TeamService;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import faang.school.projectservice.validator.InternshipServiceValidator;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {

    private static final Long INTERNSHIP_DTO_ID = 222L;
    private static final Long MENTOR_ID = 1L;
    private static final List<Long> INTERN_IDS = List.of(2L, 3L, 4L, 5L);
    private static final String INTERNSHIP_DTO_NAME = "dtoName";
    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, 4, 1, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2024, 6, 1, 0, 0, 0);
    private static final Long PROJECT_ID = 111L;
    private static final String PROJECT_NAME = "projectName";
    private static final String PROJECT_DESCRIPTION = "projectDescription";
    private static final Long PROJECT_OWNER_ID = 10L;
    private static final ProjectStatus PROJECT_STATUS = ProjectStatus.CREATED;
    private static final ProjectVisibility PROJECT_VISIBILITY = ProjectVisibility.PUBLIC;
    private final TeamMember mentor = TeamMember.builder().id(1L).userId(21L).roles(List.of(TeamRole.DEVELOPER)).build();
    private final TeamMember firstIntern = TeamMember.builder().id(2L).userId(23L).roles(List.of(TeamRole.INTERN)).build();
    private final TeamMember secondIntern = TeamMember.builder().id(3L).userId(24L).roles(List.of(TeamRole.INTERN)).build();
    private final TeamMember thirdIntern = TeamMember.builder().id(4L).userId(25L).roles(List.of(TeamRole.INTERN)).build();
    private final TeamMember fourthIntern = TeamMember.builder().id(5L).userId(26L).roles(List.of(TeamRole.INTERN)).build();
    private final List<TeamMember> internsList = List.of(firstIntern, secondIntern, thirdIntern, fourthIntern);
    private final Team projectTeam = Team.builder().id(55L).teamMembers(List.of(mentor)).build();
    private final Team internshipTeam = Team.builder().id(55L).teamMembers(internsList).build();
    private final List<Team> teams = List.of(projectTeam, internshipTeam);
    private final List<Task> taskList = List.of(Task.builder().id(1L).name("firstTask").performerUserId(11L).reporterUserId(111L).build(),
            Task.builder().id(2L).name("firstTask").performerUserId(2L).reporterUserId(1L).status(TaskStatus.DONE).build(),
            Task.builder().id(3L).name("firstTask").performerUserId(3L).reporterUserId(1L).status(TaskStatus.TODO).build(),
            Task.builder().id(4L).name("firstTask").performerUserId(4L).reporterUserId(1L).status(TaskStatus.DONE).build(),
            Task.builder().id(5L).name("firstTask").performerUserId(5L).reporterUserId(1L).status(TaskStatus.DONE).build());

    private final Project project = Project.builder().id(PROJECT_ID).status(PROJECT_STATUS).visibility(PROJECT_VISIBILITY).tasks(taskList)
            .teams(teams).build();

    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private InternshipServiceValidator validator;
    @Spy
    private InternshipMapperImpl internshipMapper;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private TeamService teamService;
    @Mock
    private ProjectService projectService;
    @Mock
    private List<InternshipFilter> internshipFilterList;

    @Captor
    private ArgumentCaptor<Internship> captor;

    @InjectMocks
    private InternshipServiceImpl internshipService;

    @Test
    public void testCreatePositive() {
        ProjectDto projectDto = new ProjectDto(PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION, PROJECT_OWNER_ID, List.of(2L),
                List.of(100L), null, ProjectStatusDto.IN_PROGRESS, ProjectVisibilityDto.PUBLIC);
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, MENTOR_ID,
                PROJECT_ID, InternshipStatusDto.IN_PROGRESS, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, END_DATE);

        when(projectService.getProjectById(internshipDto.ownedProjectId())).thenReturn(projectDto);
        when(teamMemberService.findById(2L)).thenReturn(firstIntern);
        when(teamMemberService.findById(3L)).thenReturn(secondIntern);
        when(teamMemberService.findById(4L)).thenReturn(thirdIntern);
        when(teamMemberService.findById(5L)).thenReturn(fourthIntern);
        when(teamMemberService.findById(1L)).thenReturn(mentor);
        when(teamService.createTeam(internsList, project)).thenReturn(internshipTeam);

        internshipService.create(internshipDto);

        verify(teamMemberService, times(5)).findById(anyLong());
        verify(projectService, times(1)).saveNewTeam(internshipTeam, internshipDto.ownedProjectId());
        verify(internshipRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testUpdatePositive() {
        ProjectDto projectDto = new ProjectDto(PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION, PROJECT_OWNER_ID, List.of(2L),
                List.of(100L), null, ProjectStatusDto.IN_PROGRESS, ProjectVisibilityDto.PUBLIC);
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, MENTOR_ID,
                PROJECT_ID, InternshipStatusDto.COMPLETED, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, END_DATE);
        Internship internship = new Internship();
        internship.setId(INTERNSHIP_DTO_ID);
        internship.setInterns(internsList);
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setName(INTERNSHIP_DTO_NAME);
        when(projectService.getProjectById(internshipDto.ownedProjectId())).thenReturn(projectDto);
        when(internshipRepository.getReferenceById(internshipDto.id())).thenReturn(internship);
        when(teamMemberService.findById(2L)).thenReturn(firstIntern);
        when(teamMemberService.findById(3L)).thenReturn(secondIntern);
        when(teamMemberService.findById(4L)).thenReturn(thirdIntern);
        when(teamMemberService.findById(5L)).thenReturn(fourthIntern);

        internshipService.update(internshipDto);

        verify(projectService, times(1)).getProjectById(internshipDto.ownedProjectId());
        verify(internshipRepository, times(1)).getReferenceById(internshipDto.id());
        verify(teamMemberService, times(4)).findById(anyLong());
        verify(teamMemberService, times(3)).save(any());
        verify(internshipRepository, times(1)).save(any());
    }
}