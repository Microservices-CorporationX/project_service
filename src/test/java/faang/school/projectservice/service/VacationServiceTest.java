package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import jakarta.validation.ValidationException;
import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.filter.FilterVacancy;
import faang.school.projectservice.mapper.MapperVacation;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyExtraRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.ValidationVacancies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.VacationDataCreatorTest;

import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacationServiceTest {
    @Mock
    private VacancyExtraRepository vacancyExtraRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private MapperVacation mapperVacation;
    @Mock
    private ValidationVacancies validationVacancies;
    @Mock
    private CandidateService candidateService;
    @Mock
    private List<FilterVacancy> filters;
    @InjectMocks
    private VacationService vacationService;

    @Mock
    private FilterVacancy mockFilterVacancy;

    @Test
    void testSaveVacationSuccess() {
        VacancyDto vacancyDtoIn = VacationDataCreatorTest.getSaveInputVacancyDto();
        VacancyDto vacancyDtoOut = VacationDataCreatorTest.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacationDataCreatorTest.getServiceVacancy(1L);

        when(mapperVacation.vacancyDToToVacancy(vacancyDtoIn)).thenReturn(vacancy);
        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(mapperVacation.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);

        VacancyDto result = vacationService.saveVacation(vacancyDtoIn);

        verify(validationVacancies, times(1)).isProjectExist(vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.createdBy(),
                "createdBy", vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.updatedBy(),
                "updatedBy", vacancyDtoIn.projectId());

        assertEquals(vacancyDtoOut, result);
    }

    @Test
    void testUpdateVacationSuccess() {
        testUpdateVacation(VacationDataCreatorTest.getSaveOutputVacancyDto(1));
    }

    @Test
    void testUpdateVacationStatusClosedSuccess() {
        testUpdateVacation(VacationDataCreatorTest.getSaveOutputVacancyDtoStatusClose(1));
    }

    @Test
    void deleteVacation() {
        VacancyDto vacancyDtoIn = VacationDataCreatorTest.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOut = VacationDataCreatorTest.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacationDataCreatorTest.getServiceVacancy(1L);

        when(vacancyExtraRepository.findById(vacancyDtoIn.id())).thenReturn(vacancy);
        when(mapperVacation.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);
        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);

        VacancyDto result = vacationService.deleteVacation(vacancyDtoIn.id());

        assertEquals(vacancyDtoOut, result);
    }

    @Test
    void findByFilter() {
        FilterVacancyDto filterVacancyDto = VacationDataCreatorTest.getFilter();
        List<VacancyDto> expectedDtos = VacationDataCreatorTest.getListVacancyDto(2);
        List<Vacancy> vacancyList = List.of(VacationDataCreatorTest.getServiceVacancy(1L),
                                            VacationDataCreatorTest.getServiceVacancy(2L));


        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.findAll()).thenReturn(vacancyList);

        when(mockFilterVacancy.isAvailable(filterVacancyDto)).thenReturn(true);
        when(vacancyRepository.findAll()).thenReturn(vacancyList);
        when(filters.stream()).thenReturn(Stream.of(mockFilterVacancy));
        when(mockFilterVacancy.isAvailable(filterVacancyDto)).thenReturn(true);
        when(mockFilterVacancy.apply(any(Stream.class), eq(filterVacancyDto))).thenReturn(vacancyList.stream());

        when(mapperVacation.vacancyToVacancyDTo(any(Vacancy.class)))
                .thenAnswer(invocation -> expectedDtos.get(vacancyList.indexOf(invocation.getArgument(0))));

        List<VacancyDto> result = vacationService.findByFilter(filterVacancyDto);
        assertEquals(expectedDtos, result);
        verify(mockFilterVacancy, times(1)).apply(any(Stream.class), eq(filterVacancyDto));
    }

    @Test
    void testGetVacancyByIdSuccess() {
        VacancyDto vacancyDtoIn = VacationDataCreatorTest.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOut = VacationDataCreatorTest.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacationDataCreatorTest.getServiceVacancy(1L);

        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.findById(vacancyDtoIn.id())).thenReturn(Optional.of(vacancy));
        when(mapperVacation.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);

        VacancyDto result = vacationService.getVacancyById(vacancyDtoIn.id());
        assertEquals(vacancyDtoOut, result);
    }

    @Test
    void testGetVacancyByIdNotFoundItemFail() {
        VacancyDto vacancyDtoIn = VacationDataCreatorTest.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOut = VacationDataCreatorTest.getSaveOutputVacancyDto(1);
        Vacancy vacancy = null;

        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.findById(vacancyDtoIn.id())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> vacationService.getVacancyById(vacancyDtoIn.id()));
    }

    private void testUpdateVacation(VacancyDto vacancyDtoIn) {
        VacancyDto vacancyDtoOut = VacationDataCreatorTest.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacationDataCreatorTest.getServiceVacancy(1L);

        when(vacancyExtraRepository.findById(vacancyDtoIn.id())).thenReturn(vacancy);
        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(mapperVacation.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);

        VacancyDto result = vacationService.updateVacation(vacancyDtoIn);

        verify(validationVacancies, times(1)).isVacancyExist(vacancyDtoIn.id());
        verify(validationVacancies, times(1)).isProjectExist(vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.createdBy(),
                "createdBy", vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.updatedBy(),
                "updatedBy", vacancyDtoIn.projectId());
        verify(vacancyExtraRepository, times(1)).findById(vacancyDtoIn.id());
        verify(mapperVacation, times(1)).update(vacancyDtoIn, vacancy);

        assertEquals(vacancyDtoOut, result);
    }
}