package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.stage.CreateStageRequest;
import faang.school.projectservice.dto.stage.DeleteStageRequest;
import faang.school.projectservice.dto.stage.StageResponse;
import faang.school.projectservice.dto.stage.UpdateStageRequest;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Spy
    private StageMapper stageMapper = new StageMapperImpl();

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private StageService stageService;

    private long projectId;
    private CreateStageRequest createStageRequest;
    private Project project;
    private Stage stage1;
    private Stage stage2;
    private Stage stage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Stage 1");

        projectId = 1L;
        createStageRequest = new CreateStageRequest("Stage 1", projectId, List.of());
        // Тестовый проект
        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");


        stage1 = new Stage();
        stage1.setStageName("Stage 1");
        stage1.setTasks(new ArrayList<>());
        stage1.setStageRoles(new ArrayList<>());

        stage2 = new Stage();
        stage2.setStageName("Stage 2");
        stage2.setTasks(new ArrayList<>());
        stage2.setStageRoles(new ArrayList<>());

        project.setStages(List.of(stage1, stage2));
    }

    @Test
    void testCreateStage_ProjectNotFound() {

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> stageService.create(createStageRequest),
                "Expected exception for missing project");

        assertTrue(exception.getMessage().contains("Project with id"), "Exception message should indicate missing project");

        verify(stageRepository, never()).save(any(Stage.class));
    }


    @Test
    void testDeleteStageWithStrategy_NullRequest() {
        assertThrows(IllegalArgumentException.class, () -> stageService.deleteStageWithStrategy(null));
        verifyNoInteractions(stageRepository, taskRepository);
    }

    @Test
    void testDeleteStageWithStrategy_NullStageId() {
        DeleteStageRequest request = DeleteStageRequest.builder()
                .stageId(null)
                .deletionStrategy("CASCADE")
                .build();

        assertThrows(IllegalArgumentException.class, () -> stageService.deleteStageWithStrategy(request));
        verifyNoInteractions(stageRepository, taskRepository);
    }

    @Test
    void testDeleteStageWithStrategy_EmptyDeletionStrategy() {
        DeleteStageRequest request = DeleteStageRequest.builder()
                .stageId(1L)
                .deletionStrategy("")
                .build();

        assertThrows(DataValidationException.class, () -> stageService.deleteStageWithStrategy(request));
        verifyNoInteractions(stageRepository, taskRepository);
    }

    @Test
    void testDeleteStageWithStrategy_InvalidDeletionStrategy() {
        DeleteStageRequest request = DeleteStageRequest.builder()
                .stageId(1L)
                .deletionStrategy("INVALID")
                .build();

        assertThrows(DataValidationException.class, () -> stageService.deleteStageWithStrategy(request));
        verifyNoInteractions(stageRepository, taskRepository);
    }

}
