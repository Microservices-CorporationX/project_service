package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.impl.MomentServiceImpl;
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

@ExtendWith(MockitoExtension.class)
class MomentControllerTest {

    @Mock
    MomentServiceImpl momentServiceMock;
    @Spy
    MomentControllerValidator momentControllerValidator;
    @InjectMocks
    MomentController momentController;
    MomentRequestDto validMomentRequestDto;
    List<Long> defaultProjectsIds;
    MomentFilterDto momentFilterDto;

    @BeforeEach
    void init() {
        defaultProjectsIds = new ArrayList<>(List.of(1L, 2L, 3L));

        validMomentRequestDto = MomentRequestDto.builder()
                .id(123L)
                .name("Cool moment")
                .date("2025/01/20 11:11:22")
                .description("some description")
                .projectToAddIds(defaultProjectsIds)
                .build();

        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("2025/01/01 00:00:00")
                .dateTo("2025/12/31 23:59:59")
                .projectsIds(defaultProjectsIds)
                .build();
    }

    @Test
    @DisplayName("Test moment creation")
    void testCreate() {
        momentController.create(validMomentRequestDto);
        Mockito.verify(momentServiceMock, Mockito.times(1)).createMoment(validMomentRequestDto);
    }

    @Test
    @DisplayName("Test moment update")
    void testUpdate() {
        momentController.update(validMomentRequestDto);
        Mockito.verify(momentServiceMock, Mockito.times(1)).updateMoment(validMomentRequestDto);
    }

    @Test
    @DisplayName("Test get moments by filter")
    void testGetMoments() {
        momentController.getMoments(momentFilterDto);
        Mockito.verify(momentServiceMock, Mockito.times(1)).getMoments(momentFilterDto);
    }

    @Test
    @DisplayName("Test get all moments")
    void testGetAllMoments() {
        momentController.getAllMoments();
        Mockito.verify(momentServiceMock, Mockito.times(1)).getAllMoments();
    }

    @Test
    @DisplayName("Test get one moment")
    void testGetMoment() {
        momentController.getMoment(1L);
        Mockito.verify(momentServiceMock, Mockito.times(1)).getMoment(1L);
    }
}