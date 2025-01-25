package faang.school.projectservice.service;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @InjectMocks
    private VacancyService vacancyService;

    @Test
    void testGetVacanciesByIds() {
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setId(1L);
        vacancy1.setName("Software Engineer");
        vacancy1.setDescription("Develop software");

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setId(2L);
        vacancy2.setName("Project Manager");
        vacancy2.setDescription("Manage projects");

        when(vacancyRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(vacancy1, vacancy2));

        List<Vacancy> result = vacancyService.getVacanciesByIds(Arrays.asList(1L, 2L));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Engineer", result.get(0).getName());
        assertEquals("Project Manager", result.get(1).getName());
        verify(vacancyRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
    }
}