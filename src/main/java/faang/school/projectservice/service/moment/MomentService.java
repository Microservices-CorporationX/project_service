package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filters.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.moment.MomentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentMapper momentMapper;
    private final MomentRepository momentRepository;
    private final MomentServiceValidator momentServiceValidator;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final List<MomentFilter> momentFilters;

    public MomentDto createMoment(@Valid MomentDto momentDto) {
        Moment moment = processMoments(momentDto);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    public MomentDto updateMoment(Long momentId, @Valid MomentDto momentDto) {
        Moment existingMoment = getMomentIfExists(momentId);
        Moment updatedMoment = processMoments(momentDto);
        updatedMoment.setId(existingMoment.getId());
        Moment savedMoment = momentRepository.save(updatedMoment);
        return momentMapper.toDto(savedMoment);
    }

    public void deleteMoment(Long momentId) {
        Moment existingMoment = getMomentIfExists(momentId);
        momentRepository.delete(existingMoment);
    }

    public List<MomentDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        return moments.stream().map(momentMapper::toDto).toList();
    }

    public MomentDto getMomentById(Long momentId) {
        Moment existingMoment = getMomentIfExists(momentId);
        return momentMapper.toDto(existingMoment);
    }

    public List<MomentDto> filterMomentsByDate(MomentFilterDto filterDto) {
        Stream<Moment> moments = momentRepository.findAll().stream();

        Stream<MomentDto> momentDtoStream = momentFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(moments, filterDto))
                .map(momentMapper::toDto);

        return momentDtoStream.toList();
    }

    private Moment getMomentIfExists(Long momentId) {
        return momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Moment doesn't exist by id: %s", momentId)));
    }

    private Moment processMoments(@Valid MomentDto momentDto) {
        List<Long> projectIds = momentDto.getProjectIds();
        momentServiceValidator.validateListContainUniqueItems(projectIds, "projectIds");
        momentServiceValidator.validateProjectsExist(projectIds);

        Set<Long> teamIdsForProject = getTeamIdsForProject(projectIds);
        List<Long> teamMemberIds = momentDto.getTeamMemberIds();
        if (teamMemberIds != null && !teamMemberIds.isEmpty()) {
            momentServiceValidator.validateListContainUniqueItems(teamMemberIds, "teamMemberIds");
            momentServiceValidator.validateTeamMemberExists(teamMemberIds);
            teamIdsForProject.addAll(teamMemberIds);
        }
        momentDto.setTeamMemberIds(new ArrayList<>(teamIdsForProject));

        Set<Long> projectIdsByTeamId = getProjectIdsByTeamId(new ArrayList<>(teamIdsForProject));
        projectIdsByTeamId.addAll(projectIds);
        momentServiceValidator.validateProjectsAreActive(new ArrayList<>(projectIdsByTeamId));
        momentDto.setProjectIds(new ArrayList<>(projectIdsByTeamId));
        return momentMapper.toEntity(momentDto);
    }

    private Set<Long> getTeamIdsForProject(List<Long> projectIds) {
        return projectIds.stream()
                .flatMap(projectId -> projectRepository.getProjectById(projectId).getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream()
                                .map(TeamMember::getId))).collect(Collectors.toSet());
    }

    private Set<Long> getProjectIdsByTeamId(List<Long> teamIds) {
        return teamIds.stream()
                .map(id -> teamMemberRepository.findById(id).getTeam().getProject().getId())
                .collect(Collectors.toSet());
    }
}
