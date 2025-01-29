package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.VacancyException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    private VacancyService vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private VacancyFilter vacancyFilter = Mockito.mock(VacancyFilter.class);

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Captor
    private ArgumentCaptor<Vacancy> vacancyArgumentCaptor;

    private final List<VacancyFilter> vacancyFilters = List.of(vacancyFilter);

    @BeforeEach
    void init() {
        vacancyService = new VacancyService(vacancyRepository, projectRepository, candidateRepository, vacancyFilters, vacancyMapper, vacancyValidator);
    }

    @Test
    public void create_ShouldCreateVacancySuccessfully() {
        CreateVacancyRequest createRequest = CreateVacancyRequest.builder()
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .projectId(515L)
                .createdBy(123L)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L, 103L))
                .coverImageKey("image")
                .build();

        when(projectRepository.getReferenceById(createRequest.getProjectId())).thenReturn(Project.builder().id(515L).build());

        Vacancy createdVacancy = Vacancy.builder()
                .id(234L)
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .project(Project.builder().id(createRequest.getProjectId()).build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(123L)
                .updatedBy(123L)
                .status(VacancyStatus.OPEN)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L, 103L))
                .coverImageKey("image")
                .build();

        when(vacancyRepository.save(vacancyArgumentCaptor.capture())).thenReturn(createdVacancy);

        CreateVacancyResponse createResponse = vacancyService.create(createRequest);

        verify(vacancyMapper, times(1)).fromCreateRequest(createRequest);
        verify(vacancyValidator, times(1)).validateCreatedVacancy(vacancyArgumentCaptor.capture());
        verify(vacancyMapper, times(1)).toCreateResponse(createdVacancy);

        assertEquals("Backend-разработчик", createResponse.getName());
        assertEquals("Ищем в команду backend-разработчика на Java с опытом работы от 1 года", createResponse.getDescription());
        assertEquals(TeamRole.DEVELOPER, createResponse.getPosition());
        assertEquals(515L, createResponse.getProjectId());
        assertEquals(123L, createResponse.getCreatedBy());
        assertEquals(123L, createResponse.getUpdatedBy());
        assertEquals(VacancyStatus.OPEN, createResponse.getStatus());
        assertEquals(150000.0, createResponse.getSalary());
        assertEquals(WorkSchedule.FULL_TIME, createResponse.getWorkSchedule());
        assertEquals(1, createResponse.getCount());
        assertArrayEquals(List.of(101L, 102L, 103L).toArray(), createResponse.getRequiredSkillIds().toArray(new Long[0]));
        assertEquals("image", createResponse.getCoverImageKey());
    }

    @Test
    public void update_ShouldUpdateVacancySuccessfully() {
        UpdateVacancyRequest updateRequest = UpdateVacancyRequest.builder()
                .id(234L)
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .projectId(515L)
                .candidateIds(List.of(167L, 180L, 188L, 153L))
                .updatedBy(123L)
                .status(VacancyStatus.OPEN)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L, 103L))
                .coverImageKey("new_image")
                .build();

        when(projectRepository.getReferenceById(updateRequest.getProjectId())).thenReturn(Project.builder().id(515L).build());

        List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate(), new Candidate());
        candidates.get(0).setId(167L);
        candidates.get(1).setId(180L);
        candidates.get(2).setId(188L);
        candidates.get(3).setId(153L);

        when(candidateRepository.findAllById(updateRequest.getCandidateIds())).thenReturn(candidates);

        Vacancy updatedVacancy = Vacancy.builder()
                .id(234L)
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .project(Project.builder().id(515L).build())
                .candidates(candidates)
                .createdBy(123L)
                .updatedBy(123L)
                .status(VacancyStatus.OPEN)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L, 103L))
                .coverImageKey("new_image")
                .build();

        when(vacancyRepository.save(vacancyArgumentCaptor.capture())).thenReturn(updatedVacancy);

        UpdateVacancyResponse updateResponse = vacancyService.update(updateRequest);

        verify(vacancyMapper, times(1)).fromUpdateRequest(updateRequest);
        verify(vacancyValidator, times(1)).validateUpdatedVacancy(vacancyArgumentCaptor.capture());
        verify(vacancyMapper, times(1)).toUpdateResponse(updatedVacancy);

        assertEquals(234L, updatedVacancy.getId());
        assertEquals("Backend-разработчик", updateResponse.getName());
        assertEquals("Ищем в команду backend-разработчика на Java с опытом работы от 1 года", updateResponse.getDescription());
        assertEquals(TeamRole.DEVELOPER, updateResponse.getPosition());
        assertEquals(515L, updateResponse.getProjectId());
        assertArrayEquals(List.of(167L, 180L, 188L, 153L).toArray(), updateResponse.getCandidateIds().toArray(new Long[0]));
        assertEquals(123L, updateResponse.getCreatedBy());
        assertEquals(123L, updateResponse.getUpdatedBy());
        assertEquals(VacancyStatus.OPEN, updateResponse.getStatus());
        assertEquals(150000.0, updateResponse.getSalary());
        assertEquals(WorkSchedule.FULL_TIME, updateResponse.getWorkSchedule());
        assertEquals(1, updateResponse.getCount());
        assertArrayEquals(List.of(101L, 102L, 103L).toArray(), updateResponse.getRequiredSkillIds().toArray(new Long[0]));
        assertEquals("new_image", updateResponse.getCoverImageKey());
    }

    @Test
    public void delete_ShouldDeleteSuccessfully() {
        long id = 333L;

        List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate(), new Candidate());
        candidates.get(0).setId(167L);
        candidates.get(1).setId(180L);
        candidates.get(2).setId(188L);
        candidates.get(3).setId(153L);

        when(vacancyRepository.findById(id)).thenReturn(Optional.of(Vacancy.builder().candidates(candidates).build()));

        vacancyService.delete(id);

        verify(candidateRepository, times(1))
                .deleteAllById(candidates.stream().map(Candidate::getId).toList());

        verify(vacancyRepository, times(1)).deleteById(id);
    }

    @Test
    public void delete_ShouldThrowVacancyExceptionWhenVacancyDoesNotExist() {
        long id = 333L;

        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(VacancyException.class, () -> vacancyService.delete(333L));
    }

    @Test
    public void getById_ShouldReturnVacancySuccessfully() {
        long id = 333L;

        when(vacancyRepository.findById(id)).thenReturn(Optional.of(Vacancy.builder().candidates(new ArrayList<>()).build()));

        vacancyService.getById(id);

        verify(vacancyMapper, times(1)).toGetResponse(vacancyArgumentCaptor.capture());
    }

    @Test
    public void getById_ShouldThrowVacancyExceptionWhenVacancyDoesNotExist() {
        long id = 333L;

        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(VacancyException.class, () -> vacancyService.getById(333L));
    }

    @Test
    public void getAll_ShouldReturnAllVacanciesSuccessfully() {
        vacancyService.getAll(new VacancyFilterDto());

        verify(vacancyRepository, times(1)).findAll();
    }
}
