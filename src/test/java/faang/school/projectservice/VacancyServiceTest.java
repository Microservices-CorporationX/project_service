package faang.school.projectservice;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Spy
    private VacancyMapper vacancyMapper = new VacancyMapperImpl();
    @InjectMocks
    private VacancyService vacancyService;
    @Test
    public void testGetPositionIdIsNull() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setPositionId(null);
    Assertions.assertThrows(NullPointerException.class, () ->vacancyService.createVacancy(vacancyDto));
    }
    @Test
    public void testGetProjectIdIsNull() {
        VacancyDto vacancyDto1 = new VacancyDto();
        vacancyDto1.setProjectId(null);
        Assertions.assertThrows(NullPointerException.class, () ->vacancyService.createVacancy(vacancyDto1));
    }
    @Test
    public void testCuratorRoleIdIsNotNull () {
        VacancyDto vacancyDto2 = new VacancyDto();
        vacancyDto2.setCuratorRoleId(null);
        Assertions.assertThrows(NullPointerException.class, () ->vacancyService.createVacancy(vacancyDto2));
    }
    @Test
    public void testCuratorRoleIdIsNotZeroNotOne () {
        VacancyDto vacancyDto3 = new VacancyDto();
        dtoInitializer(vacancyDto3);
        vacancyDto3.setCuratorRoleId(2);
        Assertions.assertThrows(IllegalArgumentException.class, () ->vacancyService.createVacancy(vacancyDto3));
    }

    @Test
    void createVacancyTest() {
        VacancyDto vacancyDto = new VacancyDto();
        dtoInitializer(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        vacancyService.createVacancy(vacancyDto);
        verify(vacancyRepository, times(1)).save(vacancy);
        }

    @Test
    public void testVacancyClosedWithNullCandidates () {
        VacancyDto vacancyDto = new VacancyDto();
        dtoInitializer(vacancyDto);
        vacancyDto.setStatusId(1);
        vacancyDto.setCandidatesIds(null);
        Assertions.assertThrows(IllegalStateException.class, () ->vacancyService.updateVacancy(vacancyDto));
    }
    @Test
    public void testVacancyClosedWithLessCandidates () {
        VacancyDto vacancyDto = new VacancyDto();
        dtoInitializer(vacancyDto);
        vacancyDto.setStatusId(1);
        vacancyDto.setCandidatesIds(Collections.singletonList(3L));
        vacancyDto.setCount(3);
        Assertions.assertThrows(IllegalStateException.class, () ->vacancyService.updateVacancy(vacancyDto));
    }

   @Test
   public void updateVacancyTest () {
        VacancyDto vacancyDto = new VacancyDto();
        dtoInitializer(vacancyDto);
       Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
       when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
       vacancyService.updateVacancy(vacancyDto);
            verify(vacancyRepository, times(1)).save(vacancy);
   }
   @Test
   public void testDeleteVacancy () {
        VacancyDto vacancyDto = new VacancyDto();
        dtoInitializer(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        List <Candidate> candidates = vacancy.getCandidates();
        candidates.stream().allMatch(candidate -> candidate.getCandidateStatus() == null);
        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
       vacancyService.deleteVacancy(vacancyDto);
       verify(vacancyRepository, times(1)).deleteById(vacancy.getId());
    }

    public void dtoInitializer (VacancyDto vacancyDto) {
        vacancyDto.setCuratorRoleId(0);
        vacancyDto.setProjectId(1L);
        vacancyDto.setPositionId(3);
        vacancyDto.setId(1L);
        vacancyDto.setName("Java Developer");
        vacancyDto.setCuratorId(2L);
        vacancyDto.setCount(1);
        vacancyDto.setCandidatesIds(Collections.singletonList(3L));
        vacancyDto.setStatusId(1);
        vacancyDto.setDescription("Java Developer");
        vacancyDto.setSalary(1000.0);
        vacancyDto.setCoverImageKey("Java Developer");
        vacancyDto.setRequiredSkillIds(Collections.singletonList(1L));
    }

}
