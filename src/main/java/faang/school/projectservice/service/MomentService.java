package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectService projectService;
    private final MomentMapper momentMapper;
    private final List<Filter<Moment, MomentFilterDto>> filters;

    public void saveMoment(MomentDto momentDto) {
        List<Project> openProject = momentDto.getProjectsIds().stream()
                .filter(projectService::projectIsOpen)
                .map(projectService::getProjectById)
                .toList();
        Moment moment = momentMapper.toEntity(momentDto);
        moment.setProjects(openProject);
        momentRepository.save(moment);
    }

    public void updateMoment(MomentDto momentDto) {
        momentRepository.save(findMomentById(momentDto.getId()));
    }

    public List<MomentDto> getMoments(MomentFilterDto filterDto) {
        Stream<Moment> moments = momentRepository.findAll().stream();
        for (Filter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                moments = filter.applay(moments, filterDto);
            }
        }
        return moments.map(momentMapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments() {
        return momentRepository.findAll().stream().map(momentMapper::toDto).toList();
    }

    public MomentDto getMoment(Long momentId) {
        return momentMapper.toDto(momentRepository.getReferenceById(momentId));
    }

    private Moment findMomentById(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException("Moment with id " + momentId + " not found"));
    }
}
