package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.util.StageDataUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @InjectMocks
    private StageServiceImpl stageService;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageMapper stageMapper;

    private StageDataUtilTest stageDataUtilTest = new StageDataUtilTest();


    @Test
    public void testCreateStageProjectInValid() {

        Mockito.when(projectRepository.findById(stageDataUtilTest.getStageDto().getProjectId()))
                .thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> stageService.createStage(stageDataUtilTest.getStageDto()));
        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(0))
                .save(any());
    }

    @Test
    public void testCreateStageProjectStatusInValid() {
        Project project = stageDataUtilTest.getProject();
        project.setStatus(ProjectStatus.CANCELLED);

        Mockito.when(projectRepository.findById(stageDataUtilTest.getStageDto().getProjectId()))
                .thenReturn(Optional.ofNullable(project));

        assertThrows(DataValidationException.class,
                () -> stageService.createStage(stageDataUtilTest.getStageDto()));
        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(0))
                .save(any());
    }

    @Test
    public void testCreateStageProjectNotExistsByOwnerIdInValid() {
        Project project = stageDataUtilTest.getProject();

        Mockito.when(projectRepository.findById(stageDataUtilTest.getStageDto().getProjectId()))
                .thenReturn(Optional.ofNullable(project));
        Mockito.when(projectRepository.existsByOwnerIdAndName(anyLong(), anyString()))
                .thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> stageService.createStage(stageDataUtilTest.getStageDto()));

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsByOwnerIdAndName(anyLong(), anyString());

        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(0))
                .save(any());
    }


    @Test
    public void testCreateStageValid() {

        Mockito.when(projectRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(stageDataUtilTest.getProject()));

        Mockito.when(projectRepository.existsByOwnerIdAndName(anyLong(), anyString()))
                .thenReturn(true);

        Mockito.when(stageRolesRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(stageDataUtilTest.getStageRoles()));

        Mockito.when(stageMapper.toEntity(stageDataUtilTest.getStageDto()))
                .thenReturn(stageDataUtilTest.getStage());


        stageService.createStage(stageDataUtilTest.getStageDto());

        Mockito.verify(stageRolesRepository, Mockito.times(1))
                .findById(anyLong());

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsByOwnerIdAndName(anyLong(), anyString());

        Mockito.verify(projectRepository, Mockito.times(1))
                .findById(anyLong());
        Mockito.verify(stageRepository, Mockito.times(1))
                .save(any());
    }

}

