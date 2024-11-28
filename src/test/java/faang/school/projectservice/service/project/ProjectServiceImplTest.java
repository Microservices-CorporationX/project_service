package faang.school.projectservice.service.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectStatusDto;
import faang.school.projectservice.dto.project.ProjectVisibilityDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    private final Long PROJECT_ID = 1L;
    private final Long OWNER_ID = 11L;
    private final String PROJECT_NAME = "project";
    private final String PROJECT_DESCRIPTION = "description";

    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Spy
    private TaskMapperImpl taskMapper;
    @InjectMocks
    private ProjectServiceImpl projectService;

    Project project = null;

    @BeforeEach
    public void setUp() {
        Team firstTeam = Team.builder().id(100L).build();

        TeamMember firstTeamMember = TeamMember.builder().id(11L).userId(111L).roles(List.of(TeamRole.DEVELOPER)).team(firstTeam).build();
        TeamMember secondTeamMember = TeamMember.builder().id(22L).userId(222L).roles(List.of(TeamRole.DEVELOPER)).team(firstTeam).build();
        TeamMember thirdTeamMember = TeamMember.builder().id(33L).userId(333L).roles(List.of(TeamRole.DEVELOPER)).team(firstTeam).build();

        firstTeam.setTeamMembers(List.of(firstTeamMember, secondTeamMember, thirdTeamMember));

        Task firstTask = Task.builder().id(2L).name("firstTask").status(TaskStatus.IN_PROGRESS).reporterUserId(22L)
                .performerUserId(222L).build();

        project = Project.builder()
                .id(PROJECT_ID)
                .name(PROJECT_NAME)
                .description(PROJECT_DESCRIPTION)
                .tasks(List.of(firstTask))
                .teams(List.of(firstTeam))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    public void testProjectMapper() {
        ProjectDto projectDto = new ProjectDto(PROJECT_ID, PROJECT_NAME, PROJECT_DESCRIPTION, OWNER_ID, List.of(2L),
                List.of(100L), null, ProjectStatusDto.IN_PROGRESS, ProjectVisibilityDto.PUBLIC);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);

        ProjectDto result = projectService.getProjectById(PROJECT_ID);

        assertThat(result).isEqualTo(projectDto);
    }

}