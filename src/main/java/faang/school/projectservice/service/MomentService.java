package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentCreateDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentGetDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentMapper momentMapper;
    private final MomentRepository momentRepository;
    private final ProjectService projectService;
    private final MomentValidator momentValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final List<MomentFilter> momentFilters;

    public MomentCreateDto create(MomentCreateDto dto) {
        momentValidator.validateThatProjectsAreActive(dto.getProjectIds());

        List<Project> projects = projectService.getProjects(dto.getProjectIds());
        Moment entity = momentMapper.toEntity(dto);
        entity.setProjects(projects);
        momentRepository.save(entity);
        return momentMapper.toDto(entity);
    }

    public MomentUpdateDto update(MomentUpdateDto dto) {
        momentValidator.validateMomentUpdateDto(dto);

        Moment entity = getMomentById(dto.getMomentId());
        List<Long> currentProjectIds = entity.getProjects().stream().map(Project::getId).toList();
        addNewProjects(dto, entity, currentProjectIds);
        addNewUsers(dto, entity, currentProjectIds);
        momentRepository.save(entity);

        return dto;
    }

    public List<MomentGetDto> getFilteredMoments(MomentFilterDto filters) {
        Stream<Moment> moments = getAllMoments().stream();

        return momentFilters.stream().filter(filter -> filter.isApplicable(filters))
                .reduce(moments,
                        (momentStream, momentFilter) -> momentFilter.apply(momentStream, filters),
                        (list1, list2) -> list1)
                .map(momentMapper::toGetDto)
                .toList();
    }

    public List<MomentGetDto> getMoments() {
        return getAllMoments().stream().map(momentMapper::toGetDto).toList();
    }

    public MomentGetDto getMoment(long id) {
        return momentMapper.toGetDto(getMomentById(id));
    }

    public Moment getMomentById(long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Момент с id=" + id + " не найден"));
    }

    public List<Moment> getAllMoments() {
        return momentRepository.findAll();
    }

    private void addNewUsers(MomentUpdateDto dto, Moment entity, List<Long> currentProjectIds) {
        if (CollectionUtils.isNotEmpty(dto.getUserIds())) {
            log.info("getUserIds isNotEmpty");
            List<Project> newProjects = new ArrayList<>();
            List<Long> newUserIds = dto.getUserIds().stream().filter(id -> isUserNotAdded(entity, id)).toList();
            List<Long> newTeamMembersIds = new ArrayList<>();
            newUserIds.forEach(id -> {
                List<TeamMember> newTeamMembers = teamMemberRepository.findByUserId(id);
                newTeamMembersIds.addAll(newTeamMembers.stream().map(TeamMember::getId).toList());
                List<Team> newTeams = newTeamMembers.stream().map(TeamMember::getTeam).toList();
                List<Project> newProjectsByUser = newTeams.stream().map(Team::getProject)
                        .filter(newProject -> isProjectNotAdded(currentProjectIds, newProject.getId())).toList();
                newProjects.addAll(newProjectsByUser);
            });

            entity.getProjects().addAll(newProjects);
            entity.getTeamMemberIds().addAll(newTeamMembersIds.stream().distinct().toList());
        }
    }

    private void addNewProjects(MomentUpdateDto dto, Moment entity, List<Long> currentProjectIds) {
        if (CollectionUtils.isNotEmpty(dto.getProjectIds())) {
            List<Project> projectsFromDto = projectService.getProjects(dto.getProjectIds());
            List<Long> teamMemberIds = entity.getTeamMemberIds();
            List<Project> newProjects = projectsFromDto.stream()
                    .filter(newProject -> isProjectNotAdded(currentProjectIds, newProject.getId())).toList();
            List<Team> newTeams = newProjects.stream().flatMap(project -> project.getTeams().stream()).toList();
            List<Long> newTeamMemberIds = newTeams.stream().flatMap(
                            team -> team.getTeamMembers().stream()
                                    .map(TeamMember::getId).
                                    filter(id -> !teamMemberIds.contains(id)))
                    .toList();
            entity.getProjects().addAll(newProjects);
            entity.getTeamMemberIds().addAll(newTeamMemberIds);
        }
    }

    private static boolean isUserNotAdded(Moment entity, Long newUserId) {
        return !entity.getTeamMemberIds().contains(newUserId);
    }

    private static boolean isProjectNotAdded(List<Long> currentProjectIds, Long newProjectId) {
        return !currentProjectIds.contains(newProjectId);
    }
}
