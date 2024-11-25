package faang.school.projectservice.service.momentService;

import faang.school.projectservice.dto.momentDto.MomentDto;
import faang.school.projectservice.dto.momentDto.MomentFilterDto;
import faang.school.projectservice.exception.momentException.DataValidationException;
import faang.school.projectservice.exception.momentException.MomentNotFoundException;
import faang.school.projectservice.mapper.momentMapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.momentService.filter.MomentFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectRepository projectRepository;
    private final List<MomentFilter> momentFilters;

    public MomentDto create(MomentDto momentDto) {
        projectRepository.findAllByIds(momentDto.getProjectIds())
                .stream()
                .filter(project -> project.getStatus() == ProjectStatus.CANCELLED)
                .findFirst()
                .ifPresent(project -> {
                    log.info("Received a request to create a moment with cancelled projects!");
                    throw new DataValidationException("Moment's related project doesn't have to be Cancelled!");
                });
        Moment moment = momentMapper.toEntity(momentDto);
        List<Project> projects = projectRepository.findAllByIds(momentDto.getProjectIds());
        moment.setProjects(projects);
        Moment result = momentRepository.save(moment);
        return momentMapper.toDto(result);
    }

    public MomentDto update(long id, long userId, long projectId) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> {
                    throw new MomentNotFoundException("Moment by id {} not found");
                });
        if (Stream.concat(
                moment.getProjects().stream().map(Project::getId),
                moment.getUserIds().stream()
        ).anyMatch(entityId -> entityId == projectId || entityId.equals(userId))) {
            log.info("Received a request to update a moment with an existing project or user!");
            throw new DataValidationException("Moment cannot be updated because either user or project is already in this moment!");
        }

        List<Long> updatedUserIds = new ArrayList<>(moment.getUserIds());
        updatedUserIds.add(userId);
        moment.setUserIds(updatedUserIds);

        Project project = projectRepository.getProjectById(projectId);
        List<Project> updatedProjects = new ArrayList<>(moment.getProjects());
        updatedProjects.add(project);
        moment.setProjects(updatedProjects);
        Moment updatedMoment = momentRepository.save(moment);
        return momentMapper.toDto(updatedMoment);
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filters) {
        List<Moment> moments = momentRepository.findAll();
        momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(moments.stream(), filters));
        log.info("A request to get the list of moments by filters has been processed successfully!");
        return moments.stream()
                .map(momentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MomentDto> getMoments() {
        List<Moment> moments = momentRepository.findAll();
        log.info("A request to get the list of moments has been processed successfully!");
        return moments.stream().map(momentMapper::toDto).toList();
    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("A request to get the moment with ID {} was not processed!", id);
                    throw new MomentNotFoundException("Moment not found!");
                });
        return momentMapper.toDto(moment);
    }
}
