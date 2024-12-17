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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class MapperVacationTest {
    public static final LocalDateTime PRE_SET_LOCAL_DATE_TIME = LocalDateTime.now();

    @InjectMocks
    private MapperVacationImpl mapperVacation;

    @Test
    void vacancyToVacancyDToSuccessTest() {
        assertEquals(getVacancyDtoFromVacancyResult(), mapperVacation.vacancyToVacancyDTo(getVacancyForTransferToDto()));
    }

    @Test
    void tVacancyDToToVacancySuccessTest() {
        Vacancy vacancy = mapperVacation.vacancyDToToVacancy(getCreateVacancyDto());
        assertEquals(getCreateVacancy(), vacancy);
    }

    @Test
    void updateSuccessTest() {
        Vacancy vacancy = getUpdateVacancyInput();
        mapperVacation.update(getUpdateVacancyDto(), vacancy);
        assertEquals(getUpdateVacancyResult(), vacancy);
    }

    private VacancyDto getCreateVacancyDto() {
        return VacancyDto.builder()
                .name("Name Vacancy")
                .description("description")
                .createdAt(PRE_SET_LOCAL_DATE_TIME)
                .updatedAt(PRE_SET_LOCAL_DATE_TIME)
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(100.00)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(3)
                .projectId(1L)
                .candidates(getLongListIdForDto())
                .build();
    }

    private Vacancy getCreateVacancy() {
        return Vacancy.builder()
                .name("Name Vacancy")
                .description("description")
                .createdAt(PRE_SET_LOCAL_DATE_TIME)
                .updatedAt(PRE_SET_LOCAL_DATE_TIME)
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(100.00)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(3)
                .project(Project.builder().id(1L).build())
                .build();
    }

    private VacancyDto getUpdateVacancyDto() {
        return VacancyDto.builder()
                .id(1L)
                .name("Name Vacancy")
                .description("description")
                .createdAt(PRE_SET_LOCAL_DATE_TIME)
                .updatedAt(PRE_SET_LOCAL_DATE_TIME)
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(100.00)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(3)
                .projectId(1L)
                .candidates(getLongListIdForDto())
                .build();
    }

    private Vacancy getUpdateVacancyResult() {
        return Vacancy.builder()
                .id(1L)
                .name("Name Vacancy")
                .description("description")
                .createdAt(LocalDateTime.MIN)
                .updatedAt(PRE_SET_LOCAL_DATE_TIME)
                .createdBy(2L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(100.00)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(3)
                .project(Project.builder().id(1L).build())
                .build();
    }

    private Vacancy getUpdateVacancyInput() {
        return Vacancy.builder()
                .id(1L)
                .name("Name Vacancy 1")
                .description("description 1")
                .createdAt(LocalDateTime.MIN)
                .createdBy(2L)
                .updatedAt(LocalDateTime.MIN)
                .updatedBy(2L)
                .status(VacancyStatus.POSTPONED)
                .salary(150.00)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(4)
                .project(Project.builder().id(2L).build())
                .build();
    }

    private Vacancy getVacancyForTransferToDto() {
        return Vacancy.builder()
                .id(1L)
                .name("Name Vacancy")
                .description("description")
                .createdAt(PRE_SET_LOCAL_DATE_TIME)
                .updatedAt(PRE_SET_LOCAL_DATE_TIME)
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(100.00)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(3)
                .project(getProject())
                .candidates(getCandidates())
                .requiredSkillIds(getLongListIdForDto())
                .build();
    }

    private VacancyDto getVacancyDtoFromVacancyResult() {
        return VacancyDto.builder()
                .id(1L)
                .name("Name Vacancy")
                .description("description")
                .createdAt(PRE_SET_LOCAL_DATE_TIME)
                .updatedAt(PRE_SET_LOCAL_DATE_TIME)
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(100.00)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(3)
                .projectId(1L)
                .candidates(getLongListIdForDto())
                .requiredSkillIds(getLongListIdForDto())
                .build();
    }

    private Project getProject() {
        return new Project(
                1L,
                "project name",
                "project description",
                null,
                null,
                1L,
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

    private List<Candidate> getCandidates() {
        return getLongListIdForDto().stream().map(id -> new Candidate(
                        id,
                        id,
                        "resumeDocKey " + id,
                        "coverLetter " + id,
                        CandidateStatus.WAITING_RESPONSE,
                        new Vacancy()))
                .toList();
    }

    private List<Long> getLongListIdForDto() {
        return List.of(1L, 2L, 3L);
    }
}