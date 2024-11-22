package faang.school.projectservice.service.impl.vacancy;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final List<Filter> filters;

    @Override
    public VacancyDto create(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        Project project = projectRepository.getProjectById(vacancyDto.getIdProject());
        validateCreatedContainedInTeam(project, vacancyDto);
        validateCreatedHaveRole(vacancyDto);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    @Override
    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        Project project = projectRepository.getProjectById(vacancyDto.getIdProject());
        vacancy.setCount(vacancyDto.getCount());
        vacancy.setStatus(vacancyDto.getStatus());
        long candidateCount = countCandidatesForProject(project);
        if (candidateCount >= vacancyDto.getCount()) {
            throw new DataValidationException("Not enough candidates for the vacancy");
        }
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    public void deleteVacancy(VacancyDto vacancyDto) {
        boolean vacancyExists = vacancyRepository.existsById(vacancyDto.getId());
        if (!vacancyExists) {
            throw new DataValidationException("Vacancy with the given ID does not exist");
        }
        vacancyRepository.deleteById(vacancyDto.getId());
        Project project = projectRepository.getProjectById(vacancyDto.getIdProject());
        List<TeamMember> teamMemberList = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.INTERN))
                .toList();
        teamMemberList.forEach((teamMember) -> teamRepository.deleteById(teamMember.getId()));
        log.info("Vacancy {} deleted", vacancyDto.getName());
    }

    public List<VacancyDto> vacancyFilter(VacancyDtoFilter vacancyDtoFilter) {
        List<Vacancy> allvacancy = vacancyRepository.findAll();
        return filters.stream().filter(vacancy -> vacancy.isAplicable(vacancyDtoFilter))
                .flatMap(vacancy -> vacancy.apply(allvacancy, vacancyDtoFilter))
                .map(vacancyMapper::toDto)
                .collect(Collectors.toList());
    }

    public VacancyDto getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.getReferenceById(id);
        return vacancyMapper.toDto(vacancy);
    }

    private void validateCreatedContainedInTeam(Project project, VacancyDto vacancyDto) {
        boolean isCreatedContainedInTeam = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(vacancyDto.getCreatedBy()));
        if (!isCreatedContainedInTeam) {
            throw new DataValidationException("Creator is not on the team");
        }
    }

    private void validateCreatedHaveRole(VacancyDto vacancyDto) {
        TeamMember teamMember = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        boolean isCreatedHaveRole = teamMember.getRoles().contains(TeamRole.OWNER);
        if (!isCreatedHaveRole) {
            throw new DataValidationException("The vacancy сreator does not have a suitable role");
        }
    }

    private long countCandidatesForProject(Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.INTERN))
                .count();
    }
}