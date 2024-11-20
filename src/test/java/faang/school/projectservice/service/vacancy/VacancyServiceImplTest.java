package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.service.vacancy.filter.VacancyNameFilter;
import faang.school.projectservice.service.vacancy.filter.VacancyProjectIdFilter;
import faang.school.projectservice.validator.vacancy.VacancyServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class VacancyServiceImplTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyServiceValidator vacancyServiceValidator;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Mock
    private ProjectService projectService;

    @Mock
    private CandidateService candidateService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @BeforeEach
    void setUp() {
        List<VacancyFilter> filters = List.of(new VacancyNameFilter(), new VacancyProjectIdFilter());
        vacancyService = new VacancyServiceImpl(vacancyRepository, vacancyServiceValidator, vacancyMapper, filters, projectService, candidateService, teamService);
        Mockito.lenient().when(vacancyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getVacancy()));
    }

    @Test
    void createVacancy() {
        vacancyService.createVacancy(getVacancyDto());
        Vacancy vacancy = vacancyMapper.toEntity(getVacancyDto());
        Mockito.verify(vacancyServiceValidator).validateCreateVacancy(getVacancyDto());
        Mockito.verify(vacancyRepository).save(vacancy);
    }

    @Test
    void updateVacancyNotFound() {
        Mockito.lenient().when(vacancyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyService.updateVacancy(getVacancyDto()));
        assertEquals("Vacancy id %s not found".formatted(getVacancyDto().getId()), exception.getMessage());
    }

    @Test
    void updateVacancySuccess() {
        VacancyDto vacancyDto = getVacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setCreatedAt(null);

        vacancyService.updateVacancy(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setCandidates(List.of());
        vacancy.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Mockito.verify(vacancyRepository).save(vacancy);
    }

    @Test
    void updateVacancyClosed() {
        VacancyDto vacancyDto = getVacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setCreatedAt(null);
        vacancyDto.setStatus(VacancyStatus.CLOSED);
        vacancyDto.setCandidateIds(List.of(1L, 2L, 3L));
        vacancyDto.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setCandidates(getCandidates(vacancy));
        vacancy.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        vacancy.setProject(getProject());

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(new ArrayList<>());
        Mockito.lenient().when(teamService.findMemberByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(teamMember));
        Mockito.lenient().when(vacancyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(vacancy));

        vacancyService.updateVacancy(vacancyDto);

        Mockito.verify(vacancyServiceValidator).validateCloseVacancy(vacancyDto);
        Mockito.verify(vacancyRepository).save(vacancy);
        assertTrue(teamMember.getRoles().contains(TeamRole.DEVELOPER));
    }

    @Test
    void deleteVacancy() {
        List<Candidate> candidates = getCandidates(getVacancy()).stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.WAITING_RESPONSE) || candidate.getCandidateStatus().equals(CandidateStatus.REJECTED))
                .toList();

        Mockito.lenient().when(candidateService.findAllByVacancyId(Mockito.anyLong())).thenReturn(candidates);

        vacancyService.deleteVacancy(getVacancy().getId());

        candidates.forEach(candidate -> Mockito.verify(candidateService).deleteById(candidate.getId()));
        candidates.forEach(candidate -> Mockito.verify(teamService).deleteMemberByUserId(candidate.getId()));

        Mockito.verify(vacancyRepository).deleteById(getVacancy().getId());
    }

    @Test
    void getVacanciesNullFilter() {
        Mockito.lenient().when(vacancyRepository.findAll()).thenReturn(getVacancies());
        assertEquals(vacancyMapper.toDto(getVacancies()), vacancyService.getVacancies(null));
    }

    @Test
    void getVacanciesNullFilterFields() {
        Mockito.lenient().when(vacancyRepository.findAll()).thenReturn(getVacancies());
        assertEquals(vacancyMapper.toDto(getVacancies()), vacancyService.getVacancies(new VacancyFilterDto(null, null)));
    }

    @Test
    void getVacanciesProjectFilter() {
        Mockito.lenient().when(vacancyRepository.findAll()).thenReturn(getVacancies());
        VacancyDto vacancyDto = getVacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setCreatedAt(null);
        vacancyDto.setProjectId(2L);
        assertEquals(List.of(vacancyDto), vacancyService.getVacancies(new VacancyFilterDto(null, 2L)));
    }

    @Test
    void getVacanciesNameFilter() {
        VacancyDto vacancyDto = getVacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setCreatedAt(null);
        vacancyDto.setProjectId(2L);
        vacancyDto.setName("JAVA DEVELOPER");

        List<Vacancy> vacancies = new ArrayList<>(getVacancies());
        vacancies.forEach(vacancy -> vacancy.setName("vacancy"));

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setCandidates(List.of());
        Project project = getProject();
        project.setId(2L);
        vacancy.setProject(project);
        vacancies.add(vacancy);

        Mockito.lenient().when(vacancyRepository.findAll()).thenReturn(vacancies);
        assertEquals(List.of(vacancyDto), vacancyService.getVacancies(new VacancyFilterDto("JAVA", null)));
    }

    @Test
    void getVacancyNotFound() {
        Mockito.lenient().when(vacancyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyService.getVacancy(getVacancy().getId()));
        assertEquals("Vacancy id %s not found".formatted(getVacancy().getId()), exception.getMessage());
    }

    @Test
    void getVacancySuccess() {
        Vacancy vacancy = getVacancy();
        vacancy.setCandidates(List.of());
        Mockito.lenient().when(vacancyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(vacancy));

        VacancyDto vacancyDto = vacancyMapper.toDto(vacancy);

        assertEquals(vacancyDto, vacancyService.getVacancy(vacancy.getId()));
    }

    private VacancyDto getVacancyDto() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setProjectId(getProject().getId());
        vacancyDto.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        vacancyDto.setStatus(VacancyStatus.OPEN);
        vacancyDto.setCandidateIds(List.of());
        return vacancyDto;
    }

    private List<Vacancy> getVacancies() {
        Project project = getProject();
        project.setId(2L);

        Vacancy vacancy = getVacancy();
        vacancy.setProject(project);

        return List.of(getVacancy(), getVacancy(), vacancy);
    }

    private Vacancy getVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setProject(getProject());
        vacancy.setCandidates(List.of());
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCandidates(List.of());
        return vacancy;
    }

    private List<Candidate> getCandidates(Vacancy vacancy) {
        Candidate candidateAccepted = new Candidate(1L, 1L, "key", "cL", CandidateStatus.ACCEPTED, vacancy);
        Candidate candidateRejected = new Candidate(2L, 2L, "key", "cL", CandidateStatus.REJECTED, vacancy);
        Candidate candidateWaiting = new Candidate(3L, 3L, "key", "cL", CandidateStatus.WAITING_RESPONSE, vacancy);

        return List.of(candidateAccepted, candidateRejected, candidateWaiting);
    }

    private Project getProject() {
        Project project = new Project();
        project.setId(1L);
        return project;
    }
}