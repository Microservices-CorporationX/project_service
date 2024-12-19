package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.FilterVacancy;
import faang.school.projectservice.mapper.MapperVacancy;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyExtraRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.ValidationVacancies;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.VacancyDataCreator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    private VacancyExtraRepository vacancyExtraRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private MapperVacancy mapperVacancy;
    @Mock
    private ValidationVacancies validationVacancies;
    @Mock
    private CandidateService candidateService;
    @Mock
    private List<FilterVacancy> filters;
    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private FilterVacancy mockFilterVacancy;

    @Test
    void saveVacancySuccessTest() {
        VacancyDto vacancyDtoIn = VacancyDataCreator.getSaveInputVacancyDto();
        VacancyDto vacancyDtoOut = VacancyDataCreator.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacancyDataCreator.getServiceVacancy(1L);

        when(mapperVacancy.vacancyDToToVacancy(vacancyDtoIn)).thenReturn(vacancy);
        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(mapperVacancy.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);

        VacancyDto result = vacancyService.saveVacancy(vacancyDtoIn);

        verify(validationVacancies, times(1)).projectExist(vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.createdBy(),
                "createdBy", vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.updatedBy(),
                "updatedBy", vacancyDtoIn.projectId());

        assertEquals(vacancyDtoOut, result);
    }

    @Test
    void updateVacancySuccessTest() {
        testUpdateVacancy(VacancyDataCreator.getSaveOutputVacancyDto(1));
    }

    @Test
    void updateVacancyStatusClosedSuccessTest() {
        testUpdateVacancy(VacancyDataCreator.getSaveOutputVacancyDtoStatusClose(1));
    }

    @Test
    void deleteVacancySuccessTest() {
        VacancyDto vacancyDtoIn = VacancyDataCreator.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOut = VacancyDataCreator.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacancyDataCreator.getServiceVacancy(1L);

        when(vacancyExtraRepository.findById(vacancyDtoIn.id())).thenReturn(vacancy);
        when(mapperVacancy.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);
        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);

        VacancyDto result = vacancyService.deleteVacancy(vacancyDtoIn.id());

        assertEquals(vacancyDtoOut, result);
    }

    @Test
    void findByFilterSuccessTest() {
        FilterVacancyDto filterVacancyDto = VacancyDataCreator.getFilter();
        List<VacancyDto> expectedDtos = VacancyDataCreator.getListVacancyDto(2);
        List<Vacancy> vacancyList = List.of(VacancyDataCreator.getServiceVacancy(1L),
                VacancyDataCreator.getServiceVacancy(2L));


        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.findAll()).thenReturn(vacancyList);

        when(mockFilterVacancy.isAvailable(filterVacancyDto)).thenReturn(true);
        when(vacancyRepository.findAll()).thenReturn(vacancyList);
        when(filters.stream()).thenReturn(Stream.of(mockFilterVacancy));
        when(mockFilterVacancy.isAvailable(filterVacancyDto)).thenReturn(true);
        when(mockFilterVacancy.apply(any(Stream.class), eq(filterVacancyDto))).thenReturn(vacancyList.stream());

        when(mapperVacancy.vacancyToVacancyDTo(any(Vacancy.class)))
                .thenAnswer(invocation -> expectedDtos.get(vacancyList.indexOf(invocation.getArgument(0))));

        List<VacancyDto> result = vacancyService.findByFilter(filterVacancyDto);
        assertEquals(expectedDtos, result);
        verify(mockFilterVacancy, times(1)).apply(any(Stream.class), eq(filterVacancyDto));
    }

    @Test
    void getVacancyByIdSuccessTest() {
        VacancyDto vacancyDtoIn = VacancyDataCreator.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOut = VacancyDataCreator.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacancyDataCreator.getServiceVacancy(1L);

        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.findById(vacancyDtoIn.id())).thenReturn(Optional.of(vacancy));
        when(mapperVacancy.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);

        VacancyDto result = vacancyService.getVacancyById(vacancyDtoIn.id());
        assertEquals(vacancyDtoOut, result);
    }

    @Test
    void getVacancyByIdNotFoundItemFailTest() {
        VacancyDto vacancyDtoIn = VacancyDataCreator.getDeleteInputVacancyDto();

        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.findById(vacancyDtoIn.id())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> vacancyService.getVacancyById(vacancyDtoIn.id()));
    }

    private void testUpdateVacancy(VacancyDto vacancyDtoIn) {
        VacancyDto vacancyDtoOut = VacancyDataCreator.getSaveOutputVacancyDto(1);
        Vacancy vacancy = VacancyDataCreator.getServiceVacancy(1L);

        when(vacancyExtraRepository.findById(vacancyDtoIn.id())).thenReturn(vacancy);
        when(vacancyExtraRepository.getVacancyRepository()).thenReturn(vacancyRepository);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(mapperVacancy.vacancyToVacancyDTo(vacancy)).thenReturn(vacancyDtoOut);

        VacancyDto result = vacancyService.updateVacancy(vacancyDtoIn);

        verify(validationVacancies, times(1)).vacancyExist(vacancyDtoIn.id());
        verify(validationVacancies, times(1)).projectExist(vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.createdBy(),
                "createdBy", vacancyDtoIn.projectId());
        verify(validationVacancies, times(1)).personHasNecessaryRole(vacancyDtoIn.updatedBy(),
                "updatedBy", vacancyDtoIn.projectId());
        verify(vacancyExtraRepository, times(1)).findById(vacancyDtoIn.id());
        verify(mapperVacancy, times(1)).update(vacancyDtoIn, vacancy);

        assertEquals(vacancyDtoOut, result);
    }
}