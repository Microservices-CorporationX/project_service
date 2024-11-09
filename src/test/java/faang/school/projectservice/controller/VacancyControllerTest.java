package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.NewVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    private VacancyResponseDto responseDto;
    private NewVacancyDto newDto;
    private VacancyUpdateDto updateDto;
    private FilterVacancyDto filters;

    @BeforeEach
    void setUp() {
        newDto = createTestNewVacancyDto();
        updateDto = createTestVacancyUpdateDto();
        responseDto = createTestVacancyResponseDto();
        filters = createTestFilterVacancyDto();
    }

    @Test
    @DisplayName("Create a new vacancy successfully")
    void testCreateVacancySuccess() {
        when(vacancyService.create(newDto)).thenReturn(responseDto);

        ResponseEntity<VacancyResponseDto> resultResponse = vacancyController.createVacancy(newDto);
        VacancyResponseDto resultDto = resultResponse.getBody();

        verify(vacancyService).create(newDto);

        assertNotNull(resultResponse);
        assertNotNull(resultDto);
        assertEquals(responseDto, resultDto);
        assertEquals(HttpStatus.CREATED, resultResponse.getStatusCode());
        assertNotNull(resultDto);
        assertEquals("Vacancy 1", resultDto.getName());
    }

    @Test
    @DisplayName("Create a new vacancy fail")
    void testCreateVacancyFailWrongId() {
        when(vacancyService.create(newDto)).
                thenThrow(new EntityNotFoundException(String.format("Project with id %s doesn't exist", newDto.getProjectId())));

        Exception ex = assertThrows(EntityNotFoundException.class, () -> vacancyController.createVacancy(newDto));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());
    }

    @Test
    @DisplayName("Update vacancy status successfully")
    void testUpdateVacancyStatusSuccess() {
        responseDto.setStatus(VacancyStatus.CLOSED);
        when(vacancyService.updateVacancyStatus(updateDto)).thenReturn(responseDto);

        ResponseEntity<VacancyResponseDto> resultResponse = vacancyController.updateVacancyStatus(updateDto);
        VacancyResponseDto result = resultResponse.getBody();

        verify(vacancyService, times(1)).updateVacancyStatus(updateDto);

        assertNotNull(resultResponse);
        assertNotNull(result);
        assertEquals(VacancyStatus.CLOSED, result.getStatus());
        assertEquals(HttpStatus.OK, resultResponse.getStatusCode());
    }

    @Test
    @DisplayName("Test delete vacancy successfully")
    void testDeleteVacancySuccess() {
        long id = 1L;

        ResponseEntity<Void> result = vacancyController.deleteVacancy(id);

        verify(vacancyService, times(1)).deleteVacancy(id);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    @DisplayName("Filter vacancies successfully")
    void testFilterVacanciesSuccess() {
        when(vacancyService.filterVacancies(filters)).thenReturn(List.of(dto));

        ResponseEntity<List<VacancyDto>> resultResponse = vacancyController.filterVacancies(filters);
        List<VacancyDto> resultDto = resultResponse.getBody();

        verify(vacancyService).filterVacancies(filters);

        assertNotNull(resultResponse);
        assertEquals(List.of(dto), resultDto);
        assertEquals(HttpStatus.OK, resultResponse.getStatusCode());
        assertNotNull(resultDto);
        assertEquals("Vacancy 1", resultDto.get(0).getName());
    }

    private VacancyResponseDto createTestVacancyResponseDto() {
        return VacancyResponseDto.builder()
                .id(1L)
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .projectId(1L)
                .createdAt(LocalDateTime.now())
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L))
                .build();
    }

    private NewVacancyDto createTestNewVacancyDto() {
        return NewVacancyDto.builder()
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .projectId(1L)
                .createdById(1L)
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L))
                .build();
    }

    private VacancyUpdateDto createTestVacancyUpdateDto() {
        return VacancyUpdateDto.builder()
                .id(1L)
                .updatedById(1L)
                .status(VacancyStatus.CLOSED)
                .build();
    }

    private FilterVacancyDto createTestFilterVacancyDto() {
        return FilterVacancyDto.builder()
                .title("Vacancy 1")
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .build();
    }
}
