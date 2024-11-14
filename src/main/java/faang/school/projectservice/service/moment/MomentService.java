package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectService projectService;
    private final List<MomentFilter> momentFilters;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = momentMapper.toEntity(momentDto);
        validateProjectIsActive(moment);
        moment.setProjects(projectService.findProjectsById(momentDto.getProjectIds()));
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    public MomentDto updateMoment(MomentDto updatedMomentDto, Long momentId) {
        Moment momentToUpdate = momentRepository.findById(momentId).orElseThrow(() ->
                new EntityNotFoundException("Moment not found with id : " + momentId));
        Moment updatedMoment = momentMapper.toEntity(updatedMomentDto);
        validateProjectIsActive(updatedMoment);

        momentMapper.updateEntity(momentToUpdate, updatedMomentDto);
        momentToUpdate.setProjects(projectService.findProjectsById(updatedMomentDto.getProjectIds()));
        momentToUpdate.setUpdatedAt(LocalDateTime.now());
        momentRepository.save(momentToUpdate);

        return momentMapper.toDto(momentToUpdate);
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filters) {
        Stream<Moment> moments = momentRepository.findAll().stream();
        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(moments, (stream, filter) -> filter.apply(stream, filters), (s1, s2) -> s1)
                .map(momentMapper::toDto)
                .toList();
    }

    public List<MomentDto> getAllMoments() {
        return momentRepository.findAll().stream().map(momentMapper::toDto).toList();
    }

    public MomentDto getMomentById(Long id) {
        return momentRepository.findById(id).stream()
                .map(momentMapper::toDto)
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Moment not found with id : " + id));
    }

    private void validateProjectIsActive(Moment moment) {
        if (moment.getName() == null || moment.getName().isEmpty()) {
            throw new IllegalArgumentException("Moment name cannot be empty");
        }
        if (moment.getProjects() != null && moment.getProjects().stream().anyMatch(
                project -> project.getStatus().equals(ProjectStatus.COMPLETED)
                        || project.getStatus().equals(ProjectStatus.ON_HOLD)
                        || project.getStatus().equals(ProjectStatus.CANCELLED))) {
            throw new IllegalArgumentException("Moment can only be created for active projects");
        }
    }


}
