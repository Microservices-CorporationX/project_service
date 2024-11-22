package faang.school.projectservice.service.impl.vacancy;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceImplTest {
    private VacancyServiceImpl vacancyService;

    VacancyRepository vacancyRepository = Mockito.mock(VacancyRepository.class);
    VacancyMapper vacancyMapper = Mockito.mock(VacancyMapper.class);
    ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    TeamMemberRepository teamMemberRepository = Mockito.mock(TeamMemberRepository.class);
    TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
    Filter filtermock = Mockito.mock(Filter.class);
    List<Filter> filters = List.of(filtermock);

    Vacancy vacancy;
    Vacancy vacancy2;
    VacancyDto vacancyDto;
    VacancyDto vacancyDto2;
    VacancyDtoFilter vacancyDtoFilter;
    Project project1;
    Project project2;
    Team team1;
    Team team2;
    TeamMember teamMember;
    TeamMember teamMember2;
    List<TeamMember> teamMembers;
    List<Team> teams;
    List<TeamRole> role;
    List<TeamRole> role2;
    VacancyStatus statuses;
    VacancyStatus statuses2;
    List<Vacancy> vacancies;
    List<Vacancy> vacancies2;
    List<VacancyDto> vacancyDtos;


    @BeforeEach
    void setup() {
        vacancyService = new VacancyServiceImpl(vacancyRepository, vacancyMapper, projectRepository, teamMemberRepository, teamRepository, filters);

        role = new ArrayList<>(List.of(TeamRole.OWNER));
        role2 = new ArrayList<>(List.of(TeamRole.INTERN));
        statuses = VacancyStatus.CLOSED;
        statuses2 = VacancyStatus.OPEN;

        teamMember = TeamMember.builder().id(1L).roles(Collections.unmodifiableList(role)).build();
        teamMembers = new ArrayList<>();
        teamMembers.add(teamMember);

        team1 = Team.builder().teamMembers(teamMembers).build();
        teams = new ArrayList<>();
        teams.add(team1);
        project1 = Project.builder().id(1L).teams(teams).build();

        vacancyDto = VacancyDto.builder().id(2L).idProject(1L).createdBy(1L).count(3).status(statuses).build();

        teamMember2 = TeamMember.builder().id(1L).roles(Collections.unmodifiableList(role2)).build();
        teamMembers = new ArrayList<>();
        teamMembers.add(teamMember2);

        team2 = Team.builder().teamMembers(teamMembers).build();
        teams = new ArrayList<>();
        teams.add(team2);

        project2 = Project.builder().id(1L).teams(teams).build();

        vacancy = Vacancy.builder().id(2L).name("Java").project(project2).createdBy(1L).count(3).status(statuses).build();
        vacancies = new ArrayList<>();
        vacancies.add(vacancy);

        vacancy2 = Vacancy.builder().name("Java").status(statuses2).build();
        vacancies2 = new ArrayList<>();
        vacancies2.add(vacancy2);

        vacancyDto2 = VacancyDto.builder().name("Java").status(statuses2).build();
        vacancyDtos = new ArrayList<>();
        vacancyDtos.add(vacancyDto2);

        vacancyDtoFilter = VacancyDtoFilter.builder().name("Java").status(statuses2).build();

    }

    @Test
    void testCreate() {
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(projectRepository.getProjectById(vacancyDto.getIdProject())).thenReturn(project1);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        when(teamMemberRepository.findById(1L)).thenReturn(teamMember);
        VacancyDto result = vacancyService.create(vacancyDto);
        assertEquals(result, vacancyDto);
    }

    @Test
    void testToCheckIfTheCreatorIsMissingFromTheTeam() {
        Project projectWithoutCreator = Project.builder().id(1L).teams(Collections.emptyList()).build();
        when(projectRepository.getProjectById(vacancyDto.getIdProject())).thenReturn(projectWithoutCreator);
        assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));
    }

    @Test
    void testupdateVacancy() {
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(projectRepository.getProjectById(vacancyDto.getIdProject())).thenReturn(project1);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        VacancyDto result = vacancyService.updateVacancy(vacancyDto);
        assertEquals(result, vacancyDto);
    }

    @Test
    void testdeleteVacancy() {
        when(vacancyRepository.existsById(2L)).thenReturn(true);
        when(projectRepository.getProjectById(project2.getId())).thenReturn(project2);
        when(vacancyRepository.existsById(vacancyDto.getId())).thenReturn(true);
        teamRepository.deleteById(teamMember.getId());
        vacancyService.deleteVacancy(vacancyDto);
        verify(vacancyRepository, times(1)).deleteById(2L);
        verify(teamRepository, times(1)).deleteById(1L);
    }

    @Test
    void testvacancyFilter() {
        when(vacancyRepository.findAll()).thenReturn(vacancies2);
        when(vacancyMapper.toDto(vacancy2)).thenReturn(vacancyDto2);
        when(filters.get(0).isAplicable(vacancyDtoFilter)).thenReturn(true);
        when(filters.get(0).apply(any(), any())).thenReturn(Stream.of(vacancies2.get(0)));
        List<VacancyDto> result = vacancyService.vacancyFilter(vacancyDtoFilter);
        assertEquals(result, vacancyDtos);
    }

    @Test
    void testgetVacancyForId() {
        Long id = 2L;
        when(vacancyRepository.getReferenceById(2L)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        VacancyDto result = vacancyService.getVacancyById(id);
        assertEquals(result, vacancyDto);
    }
}