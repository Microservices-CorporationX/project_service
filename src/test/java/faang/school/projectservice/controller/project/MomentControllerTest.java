package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.moment.MomentController;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.mapper.ProjectMapperProjectDtoImpl;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    @Mock
    MomentService momentService;
    @InjectMocks
    MomentController momentController;
    @Spy
    MomentMapperImpl momentMapper;
    @Spy
    ProjectMapperProjectDtoImpl projectMapper;

    @Captor
    ArgumentCaptor<MomentDto> momentDtoCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;
    @Captor
    ArgumentCaptor<MomentFilterDto> momentFilterDtoCaptor;

    MomentDto momentDto;

    List<MomentDto> momentDtoList;

    ProjectDto firstProjectDto;
    ProjectDto secondProjectDto;

    MomentFilterDto momentFilterDto;
    List<ProjectDto> partnerProjectList = new ArrayList<>();

    @BeforeEach
    public void setupMomentDto() {
        momentDto = new MomentDto();
        momentDto.setName("Notification feature");
        momentDto.setCreatedAt(LocalDateTime.of(2023, 7, 15, 10, 30));
        firstProjectDto = new ProjectDto();
        secondProjectDto = new ProjectDto();
        partnerProjectList.add(firstProjectDto);
        partnerProjectList.add(secondProjectDto);
        momentDto.setProjects(partnerProjectList);
    }

    @BeforeEach
    public void setupMomentDtoList() {
        momentDtoList = new ArrayList<>();
        MomentDto firstMomentDto = new MomentDto();
        MomentDto secondMomentDto = new MomentDto();
        firstMomentDto.setName("Redis feature");
        secondMomentDto.setName("Kibana feature");
        momentDtoList.add(firstMomentDto);
        momentDtoList.add(secondMomentDto);
    }

    @Test
    public void testCreateWithNoMomentName() {
        momentDto.setName(" ");
        assertThrows(ValidationException.class, () -> momentController.create(momentDto));
    }

    @Test
    public void testCreateWithNoProject() {
        momentDto.setProjects(new ArrayList<>());
        assertThrows(ValidationException.class, () -> momentController.create(momentDto));
    }


    @Test
    public void testCreate() {
        firstProjectDto.setStatus(ProjectStatus.IN_PROGRESS);
        secondProjectDto.setStatus(ProjectStatus.IN_PROGRESS);
        when(momentService.create(momentDto)).thenReturn(momentDto);

        MomentDto receivedMomentDto = momentController.create(momentDto);
        verify(momentService, times(1)).create(momentDtoCaptor.capture());
        MomentDto capturedMomentDto = momentDtoCaptor.getValue();
        assertEquals(momentDto, capturedMomentDto);
    }

    @Test
    public void testUpdate() {
        when(momentService.update(momentDto)).thenReturn(momentDto);

        MomentDto receivedMomentDto = momentController.update(momentDto);
        verify(momentService, times(1)).update(momentDtoCaptor.capture());
        MomentDto capturedMomentDto = momentDtoCaptor.getValue();
        assertEquals(momentDto, capturedMomentDto);
    }

    @Test
    public void testGetAllMoments() {
        when(momentService.getAllMoments()).thenReturn(momentDtoList);

        List<MomentDto> receivedMomentDtoList = momentController.getAllMoments();
        verify(momentService, times(1)).getAllMoments();
        assertEquals(momentDtoList, receivedMomentDtoList);

    }

    @Test
    public void testGetMomentById() {
        long id = 1;
        when(momentService.getMomentById(id)).thenReturn(momentDto);

        MomentDto receivedMomentDto = momentController.getMomentById(id);
        verify(momentService, times(1)).getMomentById(longCaptor.capture());
        long capturedId = longCaptor.getValue();
        assertEquals(momentDto, receivedMomentDto);
        assertEquals(id, capturedId);
    }

    @Test
    public void testGetMomentsByFilter() {
        when(momentService.getMomentsByFilter(momentFilterDto)).thenReturn(momentDtoList);

        List<MomentDto> receivedMomentsDtoList = momentController.getMomentsByFilter(momentFilterDto);
        verify(momentService, times(1)).getMomentsByFilter(momentFilterDtoCaptor.capture());
        MomentFilterDto capturedMomentFilterDto = momentFilterDtoCaptor.getValue();
        assertEquals(momentFilterDto, capturedMomentFilterDto);
        assertEquals(momentDtoList, receivedMomentsDtoList);
    }


}
