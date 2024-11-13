package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.client.teammember.TeamMemberDto;
import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.vacancy.VacancyDuplicationException;
import faang.school.projectservice.mapper.vacancy.VacancyDtoMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private CandidateService candidateService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private VacancyDtoMapper vacancyDtoMapper;
    @Mock
    private VacancyValidator vacancyValidator;

    private Project expectedProject;
    private TeamMember expectedSupervisor;
    private Vacancy expectedVacancy;
    private VacancyDto expectedVacancyDto;

    @Test
    void getVacancy_shouldReturnVacancy_whenItExists(){
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();

        when(vacancyRepository.findById(expectedVacancy.getId())).thenReturn(Optional.of(expectedVacancy));
        when(vacancyDtoMapper.toDto(expectedVacancy)).thenReturn(expectedVacancyDto);

        // when
        VacancyDto actualVacancy = vacancyService.getVacancy(expectedVacancy.getId());

        // then
        verify(vacancyRepository, times(1)).findById(expectedVacancy.getId());

        assertNotNull(actualVacancy);
        assertThat(actualVacancy.id()).isEqualTo(expectedVacancy.getId());
        assertThat(actualVacancy.name()).isEqualTo(expectedVacancy.getName());
        assertThat(actualVacancy.description()).isEqualTo(expectedVacancy.getDescription());
        assertThat(actualVacancy.projectId()).isEqualTo(expectedVacancy.getProject().getId());
    }

    @Test
    void createVacancy_shouldCreateVacancy() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();

        when(projectService.findProject(expectedProject.getId())).thenReturn(Optional.of(expectedProject));
        when(vacancyDtoMapper.toEntity(expectedVacancyDto)).thenReturn(expectedVacancy);
        when(teamMemberService.findTeamMember(expectedSupervisor.getId())).thenReturn(Optional.of(expectedSupervisor));
        when(vacancyRepository.save(expectedVacancy)).thenReturn(expectedVacancy);
        when(vacancyDtoMapper.toDto(expectedVacancy)).thenReturn(expectedVacancyDto);

        // when
        VacancyDto actualVacancy = vacancyService.createVacancy(expectedVacancyDto);

        // then
        verify(projectService, times(1)).findProject(expectedProject.getId());
        verify(teamMemberService, times(1)).findTeamMember(expectedSupervisor.getId());
        verify(vacancyRepository, times(1)).save(expectedVacancy);

        assertNotNull(actualVacancy);
        assertThat(actualVacancy.id()).isEqualTo(expectedVacancy.getId());
        assertThat(actualVacancy.name()).isEqualTo(expectedVacancy.getName());
        assertThat(actualVacancy.description()).isEqualTo(expectedVacancy.getDescription());
        assertThat(actualVacancy.projectId()).isEqualTo(expectedVacancy.getProject().getId());
    }

    @Test
    void createVacancy_shouldThrowEntityNotFoundException_whenProjectNotFound() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();

        when(projectService.findProject(expectedVacancyDto.projectId())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vacancyService.createVacancy(expectedVacancyDto));

        // then
        verify(teamMemberService, never()).findTeamMember(anyLong());
        verify(vacancyRepository, never()).save(any());

        assertThat(exception.getMessage()).isEqualTo("Project not found");
    }

    @Test
    void createVacancy_shouldThrowEntityNotFoundException_whenTeamMemberNotFound(){
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();

        when(projectService.findProject(expectedVacancyDto.projectId())).thenReturn(Optional.of(expectedProject));
        when(teamMemberService.findTeamMember(anyLong())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vacancyService.createVacancy(expectedVacancyDto));

        //then
        verify(projectService, times(1)).findProject(any());
        verify(teamMemberService, times(1)).findTeamMember(any());
        verify(vacancyRepository, never()).save(any());

        assertThat(exception.getMessage()).isEqualTo("Team member not found");
    }

    @Test
    void createVacancy_shouldThrowVacancyDuplicationException_whenVacancyAlreadyExists(){
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancyDto = getExpectedVacancyDto();
        when(projectService.findProject(anyLong())).thenReturn(Optional.of(expectedProject));
        when(teamMemberService.findTeamMember(anyLong())).thenReturn(Optional.of(expectedSupervisor));
        doThrow(new VacancyDuplicationException("Vacancy  already exists")).when(vacancyValidator)
                .validateUniqueVacancy(expectedProject, expectedVacancyDto);

        // when
        VacancyDuplicationException exception = assertThrows(VacancyDuplicationException.class, () ->
                vacancyService.createVacancy(expectedVacancyDto));

        //then
        verify(projectService, times(1)).findProject(any());
        verify(teamMemberService, times(1)).findTeamMember(any());

        assertThat(exception.getMessage()).isEqualTo("Vacancy  already exists");
    }

    @Test
    void createVacancy_shouldThrowIllegalArgumentException_whenSupervisorHasNotOwnerOrManagerRole(){
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancyDto = getExpectedVacancyDto();
        when(projectService.findProject(anyLong())).thenReturn(Optional.of(expectedProject));
        when(teamMemberService.findTeamMember(anyLong())).thenReturn(Optional.of(expectedSupervisor));
        doThrow(new IllegalArgumentException("Supervisor does not have the required role")).when(vacancyValidator)
                .validateUniqueVacancy(expectedProject, expectedVacancyDto);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                vacancyService.createVacancy(expectedVacancyDto));

        //then
        verify(projectService, times(1)).findProject(any());
        verify(teamMemberService, times(1)).findTeamMember(any());

        assertThat(exception.getMessage()).isEqualTo("Supervisor does not have the required role");
    }

    @Test
    void updateVacancy_shouldUpdateVacancy_whenDataIsValid() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();
        VacancyDto vacancyDtoToUpdate = getVacancyDtoToUpdate(VacancyStatus.POSTPONED, TeamRole.OWNER);

        when(vacancyRepository.existsById(anyLong())).thenReturn(true);
        when(candidateService.getAllCandidatesByVacancy(anyLong())).thenReturn(List.of());
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(expectedVacancy));
        when(projectService.findProject(anyLong())).thenReturn(Optional.of(expectedProject));
        when(teamMemberService.findTeamMember(anyLong())).thenReturn(Optional.of(expectedSupervisor));
        when(vacancyDtoMapper.toDto(any())).thenReturn(vacancyDtoToUpdate);

        // when
        VacancyDto updatedVacancyDto = vacancyService.updateVacancy(vacancyDtoToUpdate);

        // then
        verify(vacancyRepository, times(1)).existsById(anyLong());
        verify(projectService, times(1)).findProject(anyLong());
        verify(teamMemberService, times(1)).findTeamMember(anyLong());
        verify(vacancyRepository, times(1)).save(expectedVacancy);

        assertNotNull(updatedVacancyDto);
        assertThat(updatedVacancyDto.projectId()).isEqualTo(vacancyDtoToUpdate.projectId());
        assertThat(updatedVacancyDto.name()).isEqualTo(vacancyDtoToUpdate.name());
        assertThat(updatedVacancyDto.description()).isEqualTo(vacancyDtoToUpdate.description());
    }

    @Test
    void updateVacancy_shouldCloseVacancy_whenRequiredNumberOfCandidatesIsRight() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();
        VacancyDto vacancyDtoToUpdate = getVacancyDtoToUpdate(VacancyStatus.CLOSED, TeamRole.OWNER);

        when(vacancyRepository.existsById(anyLong())).thenReturn(true);
        when(candidateService.getAllCandidatesByVacancy(anyLong())).thenReturn(List.of());
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(expectedVacancy));
        when(projectService.findProject(anyLong())).thenReturn(Optional.of(expectedProject));
        when(teamMemberService.findTeamMember(anyLong())).thenReturn(Optional.of(expectedSupervisor));
        when(vacancyDtoMapper.toDto(any())).thenReturn(vacancyDtoToUpdate);

        // when
        VacancyDto updatedVacancyDto = vacancyService.updateVacancy(vacancyDtoToUpdate);

        // then
        verify(vacancyRepository, times(1)).existsById(anyLong());
        verify(projectService, times(1)).findProject(anyLong());
        verify(teamMemberService, times(1)).findTeamMember(anyLong());
        verify(vacancyRepository, times(1)).save(expectedVacancy);

        assertNotNull(updatedVacancyDto);
        assertThat(updatedVacancyDto.projectId()).isEqualTo(vacancyDtoToUpdate.projectId());
        assertThat(updatedVacancyDto.name()).isEqualTo(vacancyDtoToUpdate.name());
        assertThat(updatedVacancyDto.description()).isEqualTo(vacancyDtoToUpdate.description());
    }

    @Test
    void updateVacancy_shouldThrowIllegalArgumentException_whenRequiredNumberOfCandidatesIsWrong() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();
        VacancyDto vacancyDtoToUpdate = getVacancyDtoToUpdate(VacancyStatus.CLOSED, TeamRole.MANAGER);

        when(vacancyRepository.existsById(anyLong())).thenReturn(true);
        when(candidateService.getAllCandidatesByVacancy(vacancyDtoToUpdate.id())).thenReturn(List.of(1L, 2L, 3L));
        doThrow(new IllegalArgumentException("Cannot close vacancy. Required number of candidates not met."))
                .when(vacancyValidator)
                .validateCanCloseVacancy(vacancyDtoToUpdate, vacancyDtoToUpdate.numberOfCandidates(), 3);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                vacancyService.updateVacancy(vacancyDtoToUpdate));

        // then
        verify(vacancyRepository, times(1)).existsById(any());
        verify(candidateService, times(1)).getAllCandidatesByVacancy(any());
        verify(vacancyRepository, times(1)).existsById(any());

        assertThat(exception.getMessage()).isEqualTo("Cannot close vacancy. Required number of candidates not met.");
    }

    @Test
    void deleteVacancy_shouldDeleteVacancy() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();

        when(vacancyRepository.existsById(expectedVacancyDto.id())).thenReturn(true);
        when(candidateService.getAllCandidatesByVacancy(expectedVacancy.getId())).thenReturn(List.of(1L, 2L, 3L));

        // when
        vacancyService.deleteVacancy(expectedVacancy.getId());

        // then
        verify(candidateService, times(1)).deleteCandidates(any());
    }

    @Test
    void getFilteredVacancies_shouldReturnFilteredVacanciesByName_whenFilteredByName() {
        // given
        expectedProject = getExpectedProject();
        expectedSupervisor = getExpectedSupervisor();
        expectedVacancy = getExpectedVacancy(expectedProject);
        expectedVacancyDto = getExpectedVacancyDto();
        VacancyFilter filterMock = Mockito.mock(VacancyFilter.class);
        List<VacancyFilter> filters = List.of(filterMock);
        Project project = getExpectedProjectWithVacancies();
        vacancyService = new VacancyService(vacancyRepository
                , projectService
                , candidateService
                , teamMemberService
                , vacancyDtoMapper
                , vacancyValidator
                , filters);

        when(projectService.findProject(project.getId())).thenReturn(Optional.of(project));
        when(filters.get(0).isApplicable(new VacancyFilterDto("TestVacancy", "TestDesc"))).thenReturn(true);
        when(filters.get(0).apply(any(), eq(new VacancyFilterDto("TestVacancy", "TestDesc"))))
                .thenReturn(project.getVacancies().stream());
        when(vacancyDtoMapper.toDto(any())).thenReturn(expectedVacancyDto);

        // when
        List<VacancyDto> filteredVacancies = vacancyService.getFilteredVacancies(project.getId(),
                new VacancyFilterDto("TestVacancy", "TestDesc"));

        // then
        verify(projectService, times(1)).findProject(project.getId());
        verify(filters.get(0), times(1)).isApplicable(any());
        verify(filters.get(0), times(1)).apply(any(), any());

        assertThat(filteredVacancies).hasSize(2);
        assertThat(filteredVacancies.get(0).name()).isEqualTo(project.getVacancies().get(0).getName());
    }

    private Project getExpectedProject(){
        return Project.builder()
                .id(1L)
                .name("TestProject")
                .description("TestProject")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private Project getExpectedProjectWithVacancies(){
        Vacancy firstVacancy = getExpectedVacancy(expectedProject);
        Vacancy secondVacancy = getExpectedVacancy(expectedProject);
        secondVacancy.setName("TestVacancyTwo");
        secondVacancy.setDescription("TestDescTwo");

        return Project.builder()
                .id(1L)
                .name("TestProject")
                .description("TestProject")
                .ownerId(1L)
                .vacancies(List.of(firstVacancy, secondVacancy))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private TeamMember getExpectedSupervisor(){
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }

    private Vacancy getExpectedVacancy(Project project){
        return Vacancy.builder()
                .id(1L)
                .name("TestVacancy")
                .description("TestDesc")
                .project(project)
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .count(5)
                .build();
    }

    private VacancyDto getExpectedVacancyDto() {
        return VacancyDto.builder()
                .id(1L)
                .projectId(expectedProject.getId())
                .name("TestVacancy")
                .description("TestDesc")
                .status(VacancyStatus.OPEN)
                .supervisor(TeamMemberDto.builder()
                        .id(expectedSupervisor.getId())
                        .userId(expectedSupervisor.getUserId())
                        .username("John")
                        .role(TeamRole.OWNER.toString())
                        .build())
                .numberOfCandidates(5)
                .build();
    }

    private VacancyDto getVacancyDtoToUpdate(VacancyStatus status, TeamRole role) {
        return VacancyDto.builder()
                .id(expectedVacancyDto.id())
                .projectId(2L)
                .name("UpdatedVacancy")
                .description("Updated")
                .status(status)
                .supervisor(new TeamMemberDto(1L, 1L, "Max", role.toString()))
                .numberOfCandidates(5)
                .build();
    }
}