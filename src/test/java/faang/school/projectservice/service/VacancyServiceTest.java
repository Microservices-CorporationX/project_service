package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private VacancyValidator vacancyValidator;

    @InjectMocks
    private VacancyService vacancyService;

    private VacancyDto dto;
    private Vacancy vacancy;

    @BeforeEach
    void setUp() {
        dto = createTestVacancyDto();
        vacancy = createTestVacancy();
    }

    @Test
    @DisplayName("Create a new vacancy successfully")
    void createSuccess() {
        when(vacancyMapper.toEntity(dto)).thenReturn(vacancy);
        when(projectService.getProjectById(dto.getProjectId())).thenReturn(Project.builder().id(10L).build());
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(dto);

        VacancyDto result = vacancyService.create(dto);

        assertNotNull(result);
        assertEquals(dto, result);
        assertEquals("Vacancy 1", result.getName());

        verify(vacancyValidator, times(1)).validateProjectInVacancyExists(dto);
        verify(vacancyValidator, times(1)).validateVacancyCreatorRole(dto);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @Test
    @DisplayName("Create a new vacancy with invalid project id")
    void createInvalidProjectId() {
        when(vacancyMapper.toEntity(dto)).thenReturn(vacancy);
        vacancy.setProject(null);
        when(projectService.getProjectById(dto.getProjectId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> vacancyService.create(dto));

        verify(vacancyValidator, times(1)).validateProjectInVacancyExists(dto);
        verify(vacancyValidator, times(1)).validateVacancyCreatorRole(dto);
    }

    private VacancyDto createTestVacancyDto() {
        return VacancyDto.builder()
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

    private Vacancy createTestVacancy() {
        return Vacancy.builder()
                .id(1L)
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .project(Project.builder().id(1L).build())
                .createdAt(LocalDateTime.now())
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L))
                .build();
    }

}
