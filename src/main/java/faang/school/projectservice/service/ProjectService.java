package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static faang.school.projectservice.utill.ObjectsFromIds.*;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    private final TaskService taskService;
    private final ResourceService resourceService;
    private final TeamService teamService;
    private final ScheduleService scheduleService;
    private final StageService stageService;
    private final VacancyService vacancyService;
    private final MomentService momentService;
    private final MeetService meetService;

    @Transactional
    public ProjectResponseDto createProject(ProjectCreateRequestDto projectCreateRequestDto) {
        Project project = projectMapper.toProject(projectCreateRequestDto);
        fillProjectWithData(project, projectCreateRequestDto);
        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDto(savedProject);
    }

    @Transactional
    public ProjectResponseDto updateProject(ProjectUpdateRequestDto projectUpdateRequestDto) {
        Project project = projectRepository.findById(projectUpdateRequestDto.getId())
                .orElseThrow(NoSuchElementException::new);
        projectMapper.update(project, projectUpdateRequestDto);
        fillProjectWithData(project, projectUpdateRequestDto);
        project.setUpdatedAt(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDto(savedProject);
    }

    public List<ProjectResponseDto> getAllProjects(ProjectFilterDto filters) {
        Stream<Project> projectStream = projectRepository.findAll()
                .stream();
        if (filters != null) {
            for (ProjectFilter projectFilter : projectFilters) {
                if (projectFilter.isApplicable(filters)) {
                    projectStream = projectFilter.apply(projectStream, filters);
                }
            }
        }
        return projectStream.map(projectMapper::toResponseDto)
                .toList();
    }

    public ProjectResponseDto getProjectDtoById(Long id) {
        Project project = getProjectById(id);
        return projectMapper.toResponseDto(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> getProjectsByIds(List<Long> projectIds) {
        return projectRepository.findAllById(projectIds);
    }

    private void fillProjectWithData(Project project, ProjectDto projectDto) {
        project.setParentProject(getObject(project.getParentProject(),
                projectDto.getParentProjectId(),
                Project::getId,
                this::getProjectById));
        project.setTasks(getObjects(project.getTasks(),
                projectDto.getTasksIds(),
                Task::getId,
                taskService::findTasksByIds));
        project.setResources(getObjects(project.getResources(),
                projectDto.getResourcesIds(),
                Resource::getId,
                resourceService::getResourcesByIds));
        project.setTeams(getObjects(project.getTeams(),
                projectDto.getTeamsIds(),
                Team::getId,
                teamService::getTeamsByIds));
        project.setSchedule(getObject(project.getSchedule(),
                projectDto.getScheduleId(),
                Schedule::getId,
                scheduleService::getScheduleById));
        project.setStages(getObjects(project.getStages(),
                projectDto.getStagesIds(),
                Stage::getStageId,
                stageService::getStagesByIds));
        project.setVacancies(getObjects(project.getVacancies(),
                projectDto.getVacanciesIds(),
                Vacancy::getId,
                vacancyService::getVacanciesByIds));
        project.setMoments(getObjects(project.getMoments(),
                projectDto.getMomentsIds(),
                Moment::getId,
                momentService::getMomentsByIds));
        project.setMeets(getObjects(project.getMeets(),
                projectDto.getMeetsIds(),
                Meet::getId,
                meetService::getMeetsByIds));
    }
}
