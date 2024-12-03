package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.VacationDataCreatorTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapperVacationTest {
    public static final LocalDateTime PRE_SET_LOCAL_DATE_TIME = LocalDateTime.now();
    @Mock
    CandidateRepository candidateRepository;
    @Mock
    ProjectRepository projectRepository;
    @InjectMocks
    private MapperVacationImpl mapperVacation;

    @Test
    void testVacancyToVacancyDToSuccess() {
        Vacancy vacancy = VacationDataCreatorTest.getServiceVacancy(1L);
        VacancyDto vacancyDto = VacationDataCreatorTest.getVacancyDtoMapperTest();
        assertEquals(vacancyDto, mapperVacation.vacancyToVacancyDTo(vacancy));
    }

    @Test
    void testVacancyDToToVacancySuccess() {
        VacancyDto vacancyDto = getVacancyDto();
        Vacancy vacancy = getVacancy();
        when(candidateRepository.findAllCandidateByVacancyId(vacancyDto.id())).thenReturn(getCandidates());
        when(projectRepository.findById(vacancyDto.id())).thenReturn(getProject(vacancyDto.id()));
        assertEquals(vacancy, mapperVacation.vacancyDToToVacancy(vacancyDto));
    }

    @Test
    void testUpdateSuccess() {
        VacancyDto vacancyDto = getVacancyDto();
        Vacancy vacancy = getVacancyTestUpdate();
        Vacancy vacancyReference = getVacancy();

        when(projectRepository.findById(vacancyDto.id())).thenReturn(getProject(vacancyDto.id()));

        mapperVacation.update(vacancyDto, vacancy);
        assertEquals(vacancyReference, vacancy);
    }

    public Vacancy getVacancy() {
        return new Vacancy(
                1L,
                "Name Vacancy",
                "description",
                getProject(1L),
                getCandidates(),
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                1L,
                1L,
                VacancyStatus.OPEN,
                100.00,
                WorkSchedule.FULL_TIME,
                3,
                getLongListIdForDto());
    }

    public Vacancy getVacancyTestUpdate() {
        return new Vacancy(
                1L,
                "Name Vacancy Test",
                "description Test",
                getProject(2L),
                getCandidates(),
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                1L,
                2L,
                VacancyStatus.POSTPONED,
                150.00,
                WorkSchedule.SHIFT_WORK,
                4,
                getLongListIdForDto());
    }

    public VacancyDto getVacancyDto() {
        return new VacancyDto(
                1L,
                "Name Vacancy",
                "description",
                1L,
                getLongListIdForDto(),
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                1L,
                1L,
                VacancyStatus.OPEN,
                100.00,
                WorkSchedule.FULL_TIME,
                3,
                getLongListIdForDto());
    }

    public Project getProject(Long id) {
        return new Project(
                id,
                "project name",
                "project description",
                null,
                null,
                id,
                null,
                null,
                null,
                null,
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                ProjectStatus.IN_PROGRESS,
                ProjectVisibility.PUBLIC,
                "project coverImageId",
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public List<Candidate> getCandidates() {
        return getLongListIdForDto().stream().map(id -> new Candidate(
                        id,
                        id,
                        "resumeDocKey " + id,
                        "coverLetter " + id,
                        CandidateStatus.WAITING_RESPONSE,
                        new Vacancy()))
                .toList();
    }

    public List<Long> getLongListIdForDto() {
        return List.of(1L, 2L, 3L);
    }
}