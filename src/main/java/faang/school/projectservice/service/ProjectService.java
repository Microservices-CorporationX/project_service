package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.SubProjectCreateDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectUpdateDto;
import faang.school.projectservice.dto.project.TeamMemberDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentRepository momentRepository;
    private final ProjectValidator projectValidator;
    private final List<SubProjectFilter> subProjectFilters;
    private final S3Service S3service;
    private final S3Properties s3Properties;
    private final PdfService pdfService;
    private final UserServiceClient userServiceClient;

    public ProjectReadDto create(SubProjectCreateDto createDto) {
        projectValidator.validateSubProjectCreation(createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject = projectRepository.save(subProject);
        return projectMapper.toDto(subProject);
    }

    public ProjectReadDto update(SubProjectUpdateDto updateDto) {
        Project project = getProjectById(updateDto.getId());
        projectMapper.updateEntityFromDto(updateDto, project);
        List<Project> subProjects = project.getChildren();

        projectValidator.validateSubProjectStatuses(subProjects, project.getStatus());
        projectValidator.applyPrivateVisibilityIfParentIsPrivate(subProjects, updateDto.getVisibility());

        if (projectValidator.isAllSubProjectsCompleted(subProjects)) {
            addMomentToProject(project);
        }

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectReadDto> getSubProjects(long projectId, SubProjectFilterDto filterDto) {
        Project project = getProjectById(projectId);
        List<Project> subProjects = project.getChildren();

        return subProjects.stream()
                .filter(subProject -> subProjectFilters.stream().filter(filter -> filter.isApplicable(filterDto))
                        .anyMatch(filter -> filter.filterEntity(subProject, filterDto)))
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectReadDto generateProjectPresentation(long projectId) {
        Project project = getProjectById(projectId);
        UserDto owner = userServiceClient.getUser(project.getOwnerId());

        ProjectPresentationDto dto = new ProjectPresentationDto(
                project.getName(),
                project.getCreatedAt(),
                owner.username(),
                project.getStatus().name(),
                project.getDescription(),
                project.getTasks().stream().map(Task::getName).toList(),
                formatTeams(project.getTeams())
        );

        InputStream pdfInputStream = pdfService.generateProjectPresentation(dto);
        String fileKey = "project/" + project.getId() + "/presentation.pdf";
        S3service.putFileInStore(fileKey, pdfInputStream);
        project.setPresentationFileKey(fileKey);
        project.setPresentationGeneratedAt(LocalDateTime.now());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public String getPresentationFileKey(long projectId) {
        Project project = getProjectById(projectId);
        return s3Properties.getEndpoint() + "/" + s3Properties.getBucketName() + "/" +
                project.getPresentationFileKey();
    }

    private List<List<TeamMemberDto>> formatTeams(List<Team> teams) {
        return teams.stream()
                .map(team ->
                        team.getTeamMembers().stream()
                                .map(member -> new TeamMemberDto(
                                        member.getNickname(),
                                        member.getRoles()
                                ))
                                .toList()
                )
                .toList();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект с ID "
                        + projectId + " не найден"));
    }

    private Moment getMomentByName(String name) {
        Moment probe = new Moment();
        probe.setName(name);
        Example<Moment> example = Example.of(probe);
        return momentRepository.findOne(example)
                .orElseThrow(() -> new EntityNotFoundException("Момент c названием '" + name + "' не найден"));
    }

    private void addMomentToProject(Project project) {
        List<Moment> moments = project.getMoments();
        moments.add(getMomentByName("Выполнены все подпроекты"));
        project.setMoments(moments);
    }

}
