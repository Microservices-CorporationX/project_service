package ru.corporationx.projectservice.service.moment;

import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.filters.Filter;
import ru.corporationx.projectservice.mapper.moment.MomentMapper;
import ru.corporationx.projectservice.model.dto.filter.MomentFilterDto;
import ru.corporationx.projectservice.model.dto.moment.MomentDto;
import ru.corporationx.projectservice.model.entity.Moment;
import ru.corporationx.projectservice.model.entity.Project;
import ru.corporationx.projectservice.repository.MomentRepository;
import ru.corporationx.projectservice.repository.ProjectRepository;
import ru.corporationx.projectservice.validator.moment.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;
    private final ProjectRepository projectRepository;
    private final List<Filter<Moment, MomentFilterDto>> momentFilters;

    public MomentDto createMoment(MomentDto momentDto) {
        momentValidator.validateUniqueMoment(momentDto);
        log.info("Creating moment {}", momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        moment.setCreatedAt(LocalDateTime.now());
        moment.setProjects(projectRepository.findAllByIds(momentDto.getProjectIds()));
        momentValidator.validateActiveMoment(moment);
        for (Project project : moment.getProjects()) {
            project.getMoments().add(moment);
        }
        moment = momentRepository.save(moment);
        log.info("Created moment with id: {}", moment);
        return momentMapper.toDto(moment);
    }

    public MomentDto updateMoment(MomentDto momentDto, Long momentId) {
        momentValidator.validateMomentExists(momentId);
        log.info("Updating moment id:{}", momentId);
        momentDto.setId(momentId);
        Moment moment = momentMapper.toEntity(momentDto);
        moment.setProjects(projectRepository.findAllByIds(momentDto.getProjectIds()));
        momentMapper.updateEntity(moment, momentDto);
        moment.setUpdatedAt(LocalDateTime.now());
        moment = momentRepository.save(moment);
        log.info("Updated moment with id: {}", moment);
        return momentMapper.toDto(moment);
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filters) {
        Stream<Moment> moments = momentRepository.findAll().stream();
        log.info("Getting moments by filters {}", filters);
        return momentFilters.stream()
                .filter(e -> e.isApplicable(filters))
                .reduce(moments, (stream, filter) -> filter.apply(stream, filters), (stream1, stream2) -> stream1)
                .distinct()
                .map(momentMapper::toDto)
                .toList();
    }

    public List<MomentDto> getAllMoments() {
        log.info("Getting all moments");
        return momentRepository.findAll().stream().map(momentMapper::toDto).toList();
    }

    public MomentDto getMomentById(Long id) {
        log.info("Getting moment by id {}", id);
        return momentMapper.toDto(momentRepository.findById(id)
                .orElseThrow(() ->new DataValidationException("Moment with id:" + id + " not found")));
    }

}
