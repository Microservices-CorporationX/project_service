package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.StageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @InjectMocks
    private StageService stageService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageValidator stageValidator;

    @Spy
    private StageMapper stageMapper;

    @Spy
    private ProjectMapper projectMapper;

    private Stage stage;
    private TeamMember teamMember;
    private Project project;
    private ProjectDto projectDto;
    private StageDto stageDto;
    private StageRoles stageRoles;
    private Task task;

    @BeforeEach
    public void setUp() {
        stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DESIGNER)
                .build();

        task = Task
                .builder()
                .status(TaskStatus.DONE)
                .build();

        stage = Stage
                .builder()
                .stageId(1L)
                .stageName("Stage 1")
                .stageRoles(List.of(stageRoles))
                .tasks(List.of(task))
                .project(
                        Project
                                .builder()
                                .id(1L)
                                .build()
                )
                .executors(new ArrayList<>())
                .build();

        teamMember = TeamMember
                .builder()
                .userId(1L)
                .userId(1L)
                .team(
                        Team
                                .builder()
                                .teamMembers(List.of())
                                .build()
                )
                .stages(List.of(stage))
                .build();

        project = Project
                .builder()
                .id(1L)
                .name("Project 1")
                .stages(List.of(stage))
                .description("Description 1")
                .build();

        projectDto = ProjectDto
                .builder()
                .name("Project 1")
                .description("Description 1")
                .build();

        stageDto = StageDto.builder()
                .stageName("Stage 1")
                .projectId(1L)
                .stageRoles(new ArrayList<>())
                .build();
    }

    @Test
    public void setExecutor() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(1L)).thenReturn(teamMember);

        stageService.setExecutor(1L, 1L);

        verify(stageRepository, times(1)).getById(1L);
        verify(stageRepository, times(1)).save(stage);
    }

    @Test
    public void getById() {
        when(stageRepository.getById(1L)).thenReturn(stage);

        Stage stage = stageService.getById(1L);
        assertEquals(1L, stage.getStageId());

        verify(stageRepository, times(1)).getById(1L);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenStageExists() {
        Long stageId = 1L;
        when(stageRepository.existsById(stageId)).thenReturn(true);

        assertTrue(stageService.existsById(stageId));
    }

    @Test
    void existsById_ShouldReturnFalse_WhenStageDoesNotExist() {
        Long stageId = 2L;
        when(stageRepository.existsById(stageId)).thenReturn(false);

        assertFalse(stageService.existsById(stageId));
    }

    @Test
    void testCreateStageThrowException() {
        when(projectService.getById(1L)).thenThrow(new EntityNotFoundException(
                String.format("Project not found by id: %s", 1L)));

        assertThrows(EntityNotFoundException.class, () -> projectService.getById(1L),
                String.format("Project not found by id: %s", 1L));
    }

    @Test
    void testCreateStageSuccessfully() {
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(projectService.getById(stageDto.getProjectId())).thenReturn(projectDto);
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(stageDto);

        assertNotNull(result);
        assertEquals("Stage 1", result.getStageName());
    }

    @Test
    void testGetStagesByProjectIdRoleAndStatusSuccessfully() {
        when(projectService.existsById(1L)).thenReturn(true);
        when(stageRepository.findAll()).thenReturn(List.of(stage));
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        List<StageDto> result = stageService.getStagesBy(1L, "designer", "done");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Stage 1", result.get(0).getStageName());
    }

    @Test
    void testGetStagesByProjectIdRoleAndStatusInvalidProjectIdThrowEntityNotFoundException() {
        when(projectService.existsById(3L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                        stageService.getStagesBy(3L, "designer", "done"),
                String.format("Project not found by id: %s", 3L));
    }

    @Test
    void testGetStagesByProjectIdRoleAndStatusInvalidRoleThrowIllegalArgumentException() {
        when(projectService.existsById(1L)).thenReturn(true);
        when(stageService.getStagesBy(1L, "developer", "done"))
                .thenThrow(new IllegalArgumentException(String.format("Invalid role: %s", "developer")));

        assertThrows(IllegalArgumentException.class, () ->
                        stageService.getStagesBy(1L, "developer", "done"),
                String.format("Invalid role: %s", "developer"));
    }

    @Test
    void testGetStagesByProjectIdRoleAndStatusInvalidStatusThrowIllegalArgumentException() {
        when(projectService.existsById(1L)).thenReturn(true);
        when(stageService.getStagesBy(1L, "designer", "sleep"))
                .thenThrow(new IllegalArgumentException(String.format("Invalid status: %s", "in progress")));

        assertThrows(IllegalArgumentException.class, () ->
                        stageService.getStagesBy(1L, "designer", "sleep"),
                String.format("Invalid status: %s", "sleep"));
    }
}
