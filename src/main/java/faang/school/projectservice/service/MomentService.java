package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.event.project.SubProjectClosedEvent;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
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
        momentMapper.update(momentDto,updateMoment);
        return momentMapper.toDto(momentRepository.save(updateMoment));
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

    @EventListener
    public void handleSubProjectsClosedEvent(SubProjectClosedEvent event) {
        Project project = projectService.getProjectById(event.getProjectId());
        Moment moment = Moment.builder()
                .name("All subprojects completed!")
                .date(LocalDateTime.now(ZoneId.of("UTC")))
                .userIds(project.getTeams().stream()
                        .map(Team::getTeamMembers)
                        .flatMap(List::stream)
                        .map(TeamMember::getUserId)
                        .toList())
                .projects(new ArrayList<>(List.of(project)))
                .build();
        project.getMoments().add(moment);
        momentRepository.save(moment);
        log.info("Moment #{} '{}' created for project #{}", moment.getId(), moment.getName(), project.getId());
    }
}
