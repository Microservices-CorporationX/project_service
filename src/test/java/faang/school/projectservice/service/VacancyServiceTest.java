package faang.school.projectservice.service;

import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private  VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private VacancyMapper vacancyMapper;
    @InjectMocks
    VacancyService vacancyService;

    private long projectId;
    private long vacancyId;
    private long curatorId;
    private Project project;
    private TeamMember curator;

    @BeforeEach
    public void initialize() {
        projectId = 100L;
        project = Project.builder()
                .id(projectId)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        vacancyId = 200L;
        curatorId = 300L;
        curator = TeamMember.builder()
                .roles(List.of(TeamRole.OWNER))
                .id(curatorId)
                .build();
    }

    @Test
    public void createVacancyThrowsExceptionsTest() {
        Mockito.when(teamMemberRepository.findById(curatorId)).thenReturn(TeamMember.builder()
                .roles(List.of(TeamRole.DESIGNER))
                .build());
        VacancyDto vacancyDto = new VacancyDto();
        Assert.assertThrows(DataValidationException.class,
        () -> vacancyService.createVacancy(projectId, vacancyDto, curatorId));
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(Project.builder()
                .status(ProjectStatus.CANCELLED)
                .build());
        Mockito.when(teamMemberRepository.findById(curatorId)).thenReturn(curator);
        Assert.assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(projectId, vacancyDto, curatorId));
    }

    @Test
    public void createVacancyTest() {
        Mockito.when(teamMemberRepository.findById(curatorId)).thenReturn(curator);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        vacancyService.createVacancy(projectId, new VacancyDto(), curatorId);
        ArgumentCaptor<Vacancy> argumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        verify(vacancyRepository, times(1)).save(argumentCaptor.capture());
    }

    @Test
    public void getVacancyByIdThrowsExceptionTest() {
        Assert.assertThrows(EntityNotFoundException.class,
                () -> vacancyService.getVacancyById(projectId));
    }

    @Test
    public void getVacancyByIdTest() {
        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(new Vacancy()));
        vacancyService.getVacancyById(vacancyId);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    public void deleteVacancyTest() {
        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(Vacancy.builder()
                .candidates(List.of(Candidate.builder()
                                .userId(curatorId)
                                .candidateStatus(CandidateStatus.REJECTED)
                                .build()))
                .build()));
        vacancyService.deleteVacancy(vacancyId);
        verify(teamMemberRepository, times(1)).deleteById(curatorId);

    }

}
