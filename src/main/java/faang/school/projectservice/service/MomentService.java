package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectService projectService;
    private final MomentMapper momentMapper;
    private final ProjectValidator projectValidator;
    private final List<Filter<Moment, MomentFilterDto>> filters;

    public MomentDto saveMoment(MomentDto momentDto) {
        List<Project> filteredOpenProjects = Optional.ofNullable(momentDto.getProjectsIds())
                .orElse(List.of())
                .stream()
                .filter(projectValidator::isOpenProject)
                .map(projectService::getProjectById)
                .toList();
        Moment moment = momentMapper.toEntity(momentDto);
        moment.setProjects(filteredOpenProjects);
        Moment saveMoment = momentRepository.save(moment);
        return momentMapper.toDto(saveMoment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment updateMoment = findMomentById(momentDto.getId());
        List<Project> projects = projectService.findAllById(momentDto.getProjectsIds());
        updateMoment.getProjects().addAll(projects);
        momentRepository.save(updateMoment);
        return momentMapper.toDto(updateMoment);
    }

    public List<MomentDto> getMoments(MomentFilterDto filterDto) {
        Stream<Moment> moments = momentRepository.findAll().stream();
        boolean filterApplied = false;
        for (Filter<Moment, MomentFilterDto> filter : filters) {
            if (filter.isApplicable(filterDto)) {
                moments = filter.apply(moments, filterDto);
                filterApplied = true;
            }
        }
        if (!filterApplied) {
            return new ArrayList<>();
        }
        return moments.map(momentMapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments() {
        return momentRepository.findAll().stream().map(momentMapper::toDto).toList();
    }

    public MomentDto getMoment(Long momentId) {
        return momentMapper.toDto(momentRepository.getById(momentId));
    }

    private Moment findMomentById(long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException("Moment with id " + momentId + " not found"));
    }
}
