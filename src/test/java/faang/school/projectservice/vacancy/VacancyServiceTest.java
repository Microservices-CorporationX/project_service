package faang.school.projectservice.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.filter.vacancy.VacancyFilterById;
import faang.school.projectservice.filter.vacancy.VacancyFilterByName;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    CandidateRepository candidateRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Mock
    private List<VacancyFilter> vacancyFilters;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private VacancyService vacancyService;

    @Spy
    private VacancyMapperImpl vacancyMapper = new VacancyMapperImpl();

    @Test
    @DisplayName("create vacancy positive")
    void createVacancyPositiveTest() {
        Long id = 1L;
        VacancyDto actualVacancyDto = getVacancyDto();
        Project project = new Project();
        project.setOwnerId(id);
        Vacancy vacancy = vacancyMapper.vacancyDtoToVacancy(actualVacancyDto);
        vacancy.setProject(project);
        vacancy.setCreatedBy(id);
        vacancy.setStatus(VacancyStatus.OPEN);
        List<Candidate> candidates = getCandidates();
        vacancy.setCandidates(candidates);
        List<TeamMember> teamMembers = getTeamMembers();

        when(userContext.getUserId()).thenReturn(id);
        when(teamMemberJpaRepository.findByUserId(id)).thenReturn(teamMembers);
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(candidateRepository.findAllById(any())).thenReturn(candidates);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        VacancyDto inspectedVacancyDto = vacancyService.createVacancy(actualVacancyDto);
        assertEquals(inspectedVacancyDto.getId(), actualVacancyDto.getId());
        assertEquals(inspectedVacancyDto.getName(), actualVacancyDto.getName());
    }

    @Test
    @DisplayName("test update method")
    void testPositiveUpdateMethod() {
        Long id = 1L;
        VacancyDto actualVacancyDto = getVacancyDto();
        Vacancy vacancy = getVacancy(1L, "Alex");
        vacancy.setUpdatedBy(id);
        vacancy.setCount(0);

        when(vacancyRepository.findById(any())).thenReturn(Optional.of(vacancy));
        when(userContext.getUserId()).thenReturn(id);
        when(vacancyRepository.save(any())).thenReturn(vacancy);

        VacancyDto inspectedDto = vacancyService.updateVacancy(id, actualVacancyDto);
        assertEquals(inspectedDto.getId(), actualVacancyDto.getId());
        assertEquals(inspectedDto.getName(), actualVacancyDto.getName());
    }

    @Test
    @DisplayName("test negative close method")
    void testNegativeCloseMethod() {
        VacancyDto actualVacancyDto = getVacancyDto();
        actualVacancyDto.setId(1L);
        Vacancy vacancy = getVacancy(1L, "Alex");
        vacancy.setCount(1);
        vacancy.setStatus(VacancyStatus.CLOSED);
        List<Candidate> candidates = getCandidates();
        candidates.forEach(candidate -> candidate.setCandidateStatus(CandidateStatus.ACCEPTED));

        when(vacancyRepository.findById(any())).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(any())).thenReturn(vacancy);
        when(vacancyMapper.vacancyToVacancyDto(any())).thenReturn(actualVacancyDto);

        VacancyDto inspectedDto = vacancyService.closeVacancy(actualVacancyDto);
        assertEquals(inspectedDto.getName(), actualVacancyDto.getName());
        assertEquals(inspectedDto.getId(), actualVacancyDto.getId());
    }

    @Test
    @DisplayName("test positive close method")
    void testPositiveCloseMethod() {
        VacancyDto actualVacancyDto = getVacancyDto();
        actualVacancyDto.setId(1L);
        Vacancy vacancy = getVacancy(1L, "Alex");
        vacancy.setCount(1);
        vacancy.setStatus(VacancyStatus.CLOSED);
        List<Candidate> candidates = getCandidates();
        candidates.forEach(candidate -> {
            candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
            candidate.setVacancy(vacancy);
        });
        vacancy.setCandidates(candidates);

        when(vacancyRepository.findById(any())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.vacancyToVacancyDto(any())).thenReturn(actualVacancyDto);

        VacancyDto inspectedDto = vacancyService.closeVacancy(actualVacancyDto);
        assertEquals(inspectedDto.getName(), actualVacancyDto.getName());
        assertEquals(inspectedDto.getId(), actualVacancyDto.getId());
    }

    @Test
    @DisplayName("positive test getVacancyByFilters")
    void testPositiveGetVacancyIdsByFilters() {
        VacancyFilter filterById = mock(VacancyFilterById.class);
        VacancyFilter filterByName = mock(VacancyFilterByName.class);

        List<Vacancy> actualVacancies = List.of(getVacancy(1L, "Alex"));
        List<Vacancy> vacancies = getVacanciesStream().toList();
        VacancyFilterDto vacancyFilterDto = getFilterDto();
        VacancyDto vacancyDto = getVacancyDto();

        when(filterById.isApplicable(any())).thenReturn(true);
        when(filterById.apply(any(), any())).thenReturn(actualVacancies.stream());
        when(filterByName.isApplicable(any())).thenReturn(true);
        when(filterByName.apply(any(), any())).thenReturn(actualVacancies.stream());

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilters.stream()).thenReturn(Stream.of(filterById, filterByName));
        when(vacancyMapper.vacancyToVacancyDto(any())).thenReturn(vacancyDto);

        List<VacancyDto> inspectVacanciesDto = vacancyService.getVacancyIdsByFilters(vacancyFilterDto);
        List<VacancyDto> actualVacanciesDto = List.of(vacancyDto);

        assertEquals(actualVacanciesDto.size(), inspectVacanciesDto.size());
        assertEquals(actualVacanciesDto.get(0), inspectVacanciesDto.get(0));
        assertTrue(inspectVacanciesDto.contains(vacancyDto));
        assertTrue(actualVacanciesDto.size() == 1);
    }

    private Stream<Vacancy> getVacanciesStream() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name("Alex").build();
        Vacancy vacancy1 = Vacancy.builder()
                .id(2L)
                .name("Evgen").build();
        Vacancy vacancy2 = Vacancy.builder()
                .id(3L)
                .name("Alex").build();
        Vacancy vacancy3 = Vacancy.builder()
                .id(4L)
                .name("Olga").build();
        return Stream.of(vacancy, vacancy1, vacancy2, vacancy3);
    }

    private List<Candidate> getCandidates() {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        Candidate candidate1 = new Candidate();
        candidate1.setId(2L);
        Candidate candidate2 = new Candidate();
        candidate1.setId(2L);
        return Arrays.asList(candidate, candidate1, candidate2);
    }

    private VacancyFilterDto getFilterDto() {
        return VacancyFilterDto.builder()
                .id(1L)
                .namePattern("Alex").build();
    }

    private VacancyDto getVacancyDto() {
        return VacancyDto.builder()
                .id(1L)
                .name("Alex")
                .projectId(1L)
                .candidateIds(List.of(1L, 2L))
                .build();
    }

    private Vacancy getVacancy(Long id, String name) {
        return Vacancy.builder()
                .id(id)
                .name(name)
                .candidates(new ArrayList<>())
                .build();
    }

    private List<TeamMember> getTeamMembers() {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(new ArrayList<>());
        teamMember.getRoles().add(TeamRole.OWNER);
        teamMember.getRoles().add(TeamRole.DESIGNER);
        return List.of(teamMember);
    }
}
