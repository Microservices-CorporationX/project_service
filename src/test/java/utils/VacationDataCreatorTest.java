package utils;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.filter.FilterVacancy;
import faang.school.projectservice.filter.FilterVacancyName;
import faang.school.projectservice.filter.FilterVacancySalary;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VacationDataCreatorTest {
    public static final LocalDateTime PRE_SET_LOCAL_DATE_TIME = LocalDateTime.now();

    public static VacancyDto getSaveInputVacancyDto() {
        return new VacancyDto(
                null,
                "Name Vacancy 1",
                "description 1",
                1L,
                getLongList(),
                null,
                null,
                1L,
                null,
                VacancyStatus.OPEN,
                100.00,
                WorkSchedule.FULL_TIME,
                3,
                getLongList());
    }

    public static Vacancy getServiceVacancy(Long numberElement) {
        return new Vacancy(
                1L + numberElement,
                "Name Vacancy " + numberElement,
                "description " + numberElement,
                getServiceProject(numberElement),
                getCandidates(),
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                1L,
                1L,
                VacancyStatus.OPEN,
                100.00,
                WorkSchedule.FULL_TIME,
                3,
                getLongList());
    }

    public static VacancyDto getVacancyDtoMapperTest() {
        return new VacancyDto(
                2L,
                "Name Vacancy 1",
                "description 1",
                1L,
                getLongList(),
                PRE_SET_LOCAL_DATE_TIME,
                PRE_SET_LOCAL_DATE_TIME,
                1L,
                1L,
                VacancyStatus.OPEN,
                100.00,
                WorkSchedule.FULL_TIME,
                3,
                getLongList());
    }

    public static VacancyDto getSaveOutputVacancyDto(int numberElement) {
        return getVacancyDto(numberElement, VacancyStatus.OPEN);
    }

    public static VacancyDto getSaveOutputVacancyDtoStatusClose(int numberElement) {
        return getVacancyDto(numberElement, VacancyStatus.CLOSED);
    }

    public static VacancyDto getVacancyDto(int numberElement, VacancyStatus vacancyStatus ) {
        return new VacancyDto(
                1L + numberElement,
                "Name Vacancy " + numberElement,
                "description " + numberElement,
                1L,
                getLongList(),
                null,
                null,
                1L,
                null,
                vacancyStatus,
                100.00,
                WorkSchedule.FULL_TIME,
                3,
                getLongList());
    }

    public static VacancyDto getDeleteInputVacancyDto() {
        return new VacancyDto(
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static List<Long> getLongList() {
        return List.of(1L, 2L, 3L);
    }

    public static List<Candidate> getCandidates() {
        return getLongList().stream().map(id -> new Candidate(
                        id,
                        id,
                        "resumeDocKey " + id,
                        "coverLetter " + id,
                        CandidateStatus.WAITING_RESPONSE,
                        new Vacancy()))
                .toList();
    }
    public static FilterVacancyDto getFilter() {
        return new FilterVacancyDto("1", 1.0);
    }

    public static List<VacancyDto> getListVacancyDto(int numberItem) {
        List<VacancyDto> vacancyDtoList = new ArrayList<>();
        for (int i = 0; i < numberItem; i++) {
            vacancyDtoList.add(getSaveOutputVacancyDto(i));
        }
        return vacancyDtoList;
    }

    public static List<FilterVacancy> setSetFilter() {
        return List.of(new FilterVacancyName(), new FilterVacancySalary());
    }

    public static Project getServiceProject(Long id) {
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

}