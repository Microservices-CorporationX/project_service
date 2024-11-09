package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.NewVacancyDto;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.vacancy.VacancyTitleFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.VacancyValidator;
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
import java.util.Optional;

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
    private CandidateService candidateService;

    @Mock
    private VacancyValidator vacancyValidator;

    @Mock
    private ProjectValidator projectValidator;

    @InjectMocks
    private VacancyService vacancyService;

    private List<Filter<Vacancy, FilterVacancyDto>> vacancyFilters;
    private VacancyResponseDto dto;
    private NewVacancyDto newDto;
    private VacancyUpdateDto updateDto;
    private Vacancy vacancy;

    @BeforeEach
    void setUp() {
        newDto = createTestNewVacancyDto();
        updateDto = createTestVacancyUpdateDto();
        dto = createTestVacancyDto();
        vacancy = createTestVacancy();
        Filter<Vacancy, FilterVacancyDto> filterMock = mock(VacancyTitleFilter.class);
        vacancyFilters = List.of(filterMock);
    }

    @Test
    @DisplayName("Create a new vacancy successfully")
    void testCreateSuccess() {
        when(vacancyMapper.toEntity(newDto)).thenReturn(vacancy);
        when(projectService.getProjectById(newDto.getProjectId())).thenReturn(Project.builder().id(10L).build());
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(dto);

        VacancyResponseDto result = vacancyService.create(newDto);

        assertNotNull(result);
        assertEquals(dto, result);
        assertEquals("Vacancy 1", result.getName());

        verify(projectValidator, times(1)).validateProjectExistsById(newDto.getProjectId());
        verify(vacancyValidator, times(1)).validateVacancyManagerRole(newDto.getCreatedById());
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @Test
    @DisplayName("Create a new vacancy with invalid project id")
    void testCreateInvalidProjectId() {
        when(vacancyMapper.toEntity(newDto)).thenReturn(vacancy);
        vacancy.setProject(null);
        when(projectService.getProjectById(newDto.getProjectId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> vacancyService.create(newDto));

        verify(projectValidator, times(1)).validateProjectExistsById(newDto.getProjectId());
        verify(vacancyValidator, times(1)).validateVacancyManagerRole(newDto.getCreatedById());
        verify(vacancyRepository, never()).save(vacancy);
    }

    @Test
    @DisplayName("Update vacancy status successfully")
    void testUpdateVacancyStatusSuccess() {
        dto.setStatus(VacancyStatus.CLOSED);
        when(vacancyRepository.findById(updateDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(dto);

        VacancyResponseDto result = vacancyService.updateVacancyStatus(updateDto);

        verify(vacancyValidator, times(1)).validateVacancyManagerRole(updateDto.getUpdatedById());
        verify(vacancyValidator, times(1)).validateCandidateCountForClosure(vacancy);
        verify(vacancyRepository, times(1)).save(vacancy);

        assertNotNull(result);
        assertEquals(dto, result);
        assertEquals(VacancyStatus.CLOSED, result.getStatus());
    }

    @Test
    @DisplayName("Delete vacancy successful")
    void testDeleteVacancySuccess() {
        Candidate candidateOne = new Candidate();
        Candidate candidateTwo = new Candidate();
        candidateOne.setId(1L);
        candidateOne.setVacancy(vacancy);
        candidateTwo.setId(2L);
        candidateTwo.setVacancy(vacancy);
        vacancy.setCandidates(List.of(candidateOne, candidateTwo));
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));

        vacancyService.deleteVacancy(vacancy.getId());

        verify(vacancyValidator, times(1)).validateVacancyExistsById(vacancy.getId());
        verify(candidateService, times(2)).deleteCandidateById(anyLong());
        verify(vacancyRepository, times(1)).deleteById(vacancy.getId());
    }

    @Test
    @DisplayName("Update vacancy status with invalid id")
    void testUpdateVacancyStatusInvalidId() {
        when(vacancyRepository.findById(updateDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.updateVacancyStatus(updateDto));

        verify(vacancyValidator, times(1)).validateVacancyManagerRole(updateDto.getUpdatedById());
    }

    @Test
    @DisplayName("Filter vacancies: 2 in, 2 out")
    void testFilterVacanciesTwoInTwoOut() {
        vacancyServiceInit();
        FilterVacancyDto filterDto = FilterVacancyDto.builder().title("Foo").build();
        List<Vacancy> vacancies = List.of(
                Vacancy.builder().name("Foo").build(),
                Vacancy.builder().name("Bar").build()
        );
        List<VacancyResponseDto> vacanciesDto = List.of(
                VacancyResponseDto.builder().name("Foo").build(),
                VacancyResponseDto.builder().name("Bar").build()
        );

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(vacancyFilters.get(0).apply(any(), any())).thenReturn(vacancies.stream());
        when(vacancyMapper.toDto(vacancies.get(0))).thenReturn(vacanciesDto.get(0));
        when(vacancyMapper.toDto(vacancies.get(1))).thenReturn(vacanciesDto.get(1));

        List<VacancyResponseDto> result = vacancyService.filterVacancies(filterDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Foo", result.get(0).getName());
        assertEquals("Bar", result.get(1).getName());
    }

    @Test
    @DisplayName("Filter vacancies: 2 in, 1 out")
    void testFilterVacanciesTwoInOneOut() {
        vacancyServiceInit();
        FilterVacancyDto filterDto = FilterVacancyDto.builder().title("Foo").build();
        List<Vacancy> vacancies = List.of(
                Vacancy.builder().name("Foo").build()
        );
        List<VacancyResponseDto> vacanciesDto = List.of(
                VacancyResponseDto.builder().name("Foo").build(),
                VacancyResponseDto.builder().name("Bar").build()
        );

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(vacancyFilters.get(0).apply(any(), any())).thenReturn(vacancies.stream());
        when(vacancyMapper.toDto(vacancies.get(0))).thenReturn(vacanciesDto.get(0));

        List<VacancyResponseDto> result = vacancyService.filterVacancies(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Foo", result.get(0).getName());
    }

    @Test
    @DisplayName("Filter vacancies: 2 in, 0 out")
    void testFilterVacanciesTwoInZeroOut() {
        vacancyServiceInit();
        FilterVacancyDto filterDto = FilterVacancyDto.builder().title("Foo").build();
        List<Vacancy> vacancies = List.of(
                Vacancy.builder().name("Foo").build(),
                Vacancy.builder().name("Bar").build()
        );

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilters.get(0).isApplicable(filterDto)).thenReturn(false);

        List<VacancyResponseDto> result = vacancyService.filterVacancies(filterDto);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Get vacancy dto by id successfully")
    void testGetVacancyDtoByIdSuccess() {
        when(vacancyRepository.findById(vacancy.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(dto);

        VacancyDto result = vacancyService.getVacancyDtoById(vacancy.getId());

        assertNotNull(result);
        assertEquals(dto, result);
    }

    private VacancyResponseDto createTestVacancyDto() {
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

    private VacancyUpdateDto createTestVacancyUpdateDto() {
        return VacancyUpdateDto.builder()
                .id(1L)
                .updatedById(1L)
                .status(VacancyStatus.CLOSED)
                .build();
    }

    private void vacancyServiceInit() {
        vacancyService = new VacancyService(
                vacancyRepository,
                vacancyMapper,
                projectService,
                candidateService,
                vacancyValidator,
                projectValidator,
                vacancyFilters);
    }
}
