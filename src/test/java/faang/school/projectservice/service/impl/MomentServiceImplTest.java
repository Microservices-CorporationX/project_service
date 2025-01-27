package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.service.ProjectService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MomentServiceImplTest {

    @Mock
    MomentRepository momentRepositoryMock;
    @Mock
    ProjectService projectService;
    @InjectMocks
    MomentServiceImpl momentService;
    @Spy
    MomentMapperImpl momentMapper;
    @Mock
    MomentServiceValidator momentServiceValidator;


    MomentRequestDto validMomentRequestDto;
    MomentRequestDto savedMomentResponseDto;
    MomentRequestDto emptyProjectMomentRequestDto;
    MomentRequestDto incorrectDateMomentRequestDto;
    List<Long> defaultProjectsIds;

    List<MomentFilter> momentFilters = new ArrayList<>();

    MomentFilterDto momentFilterDto;

    @BeforeEach
    void setUp() {
        defaultProjectsIds = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Long> emptyProjectsIds = new ArrayList<>();

        validMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .date("20/01/2025 11:11:22")
                .description("some description")
                .projectToAddIds(defaultProjectsIds)
                .build();
        emptyProjectMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .date("20/01/2025 11:11:22")
                .description("some description")
                .projectToAddIds(emptyProjectsIds)
                .build();
        incorrectDateMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .date("20.01.2025 11:11:22")
                .description("some description")
                .projectToAddIds(emptyProjectsIds)
                .build();
        savedMomentResponseDto = MomentRequestDto.builder()
                .id(123L)
                .name("Cool moment")
                .date("20/01/2025 11:11:22")
                .description("some description")
                .projectToAddIds(defaultProjectsIds)
                .build();
        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("test")
                .dateTo("test")
                .projectsIds(defaultProjectsIds)
                .build();

        momentService = new MomentServiceImpl(momentRepositoryMock,
                momentMapper,
                momentFilters,
                projectService,
                momentServiceValidator);
    }

    @Test
    @DisplayName("Test Create Moment Positive")
    void createMoment() {
        Moment moment = momentMapper.toMomentEntity(savedMomentResponseDto);
        Mockito.when(momentRepositoryMock.save(moment)).thenReturn(moment);
        momentService.createMoment(savedMomentResponseDto);

        Mockito.verify(momentRepositoryMock, Mockito.times(1)).save(Mockito.any(Moment.class));
    }

    @Test
    @DisplayName("Test Create Moment without projects")
    void createMomentWithEmptyProjectIds() {
        Assert.assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(emptyProjectMomentRequestDto));
    }

    @Test
    @DisplayName("Test Create Moment with incorrect date")
    void createMomentWithIncorrectDate() {
        Assert.assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(incorrectDateMomentRequestDto));
    }

    @Test
    @DisplayName("Test Update Moment")
    void updateMoment() {
        Moment moment = momentMapper.toMomentEntity(validMomentRequestDto);
        Mockito.when(momentRepositoryMock.save(moment)).thenReturn(moment);
        momentService.createMoment(validMomentRequestDto);

        Mockito.verify(momentRepositoryMock, Mockito.times(1)).save(Mockito.any(Moment.class));
    }

    @Test
    @DisplayName("Test get moments")
    void getMoments() {
        momentService.getMoments(momentFilterDto);
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test Get All moments")
    void getAllMoments() {
        momentService.getAllMoments();
        Mockito.verify(momentRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test Get Moment")
    void getMoment() {
        Moment moment = momentMapper.toMomentEntity(savedMomentResponseDto);
        Mockito.when(momentRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(moment));
        momentService.getMoment(1L);

        Mockito.verify(momentRepositoryMock, Mockito.times(1)).findById(1L);
    }
}