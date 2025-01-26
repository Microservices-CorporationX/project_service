package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentCreateDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentReadDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;
    private final MomentValidator momentValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final List<MomentFilter> momentFilters;

    public MomentReadDto create(MomentCreateDto dto) {
        momentValidator.validateThatProjectsAreActive(dto.getProjectIds());

        List<Project> projects = projectRepository.findAllById(dto.getProjectIds());
        Moment entity = momentMapper.toEntity(dto);
        entity.setProjects(projects);
        momentRepository.save(entity);

        return momentMapper.toReadDto(entity);
    }

    public MomentReadDto update(MomentUpdateDto dto) {
        momentValidator.validateMomentUpdateDto(dto);

        Moment entity = getMomentById(dto.getMomentId());
        addNewProjects(dto, entity);
        addNewUsers(dto, entity);
        Moment updatedEntity = momentRepository.save(entity);

        return momentMapper.toReadDto(updatedEntity);
    }

    public List<MomentReadDto> getFilteredMoments(MomentFilterDto filters) {
        Stream<Moment> moments = getAllMoments().stream();

        List<MomentFilter> applicableMomentFilters = momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters)).toList();
        if (applicableMomentFilters.isEmpty()) {
            return List.of();
        }

        return applicableMomentFilters.stream()
                .reduce(moments,
                        (momentStream, momentFilter) -> momentFilter.apply(momentStream, filters),
                        (list1, list2) -> list1)
                .map(momentMapper::toReadDto)
                .toList();
    }

    public List<MomentReadDto> getMoments() {
        return getAllMoments().stream().map(momentMapper::toReadDto).toList();
    }

    public MomentReadDto getMoment(long id) {
        return momentMapper.toReadDto(getMomentById(id));
    }

    public Moment getMomentById(long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Момент с id=" + id + " не найден"));
    }

    public List<Moment> getAllMoments() {
        return momentRepository.findAll();
    }

    private void addNewUsers(MomentUpdateDto dto, Moment entity) {
        if (CollectionUtils.isNotEmpty(dto.getUserIds())) {
            List<Long> currentProjectIds = entity.getProjects().stream().map(Project::getId).toList();
            List<Project> newProjects = new ArrayList<>();
            List<Long> newUserIds = dto.getUserIds();
            List<Long> newTeamMembersIds = new ArrayList<>();
            newUserIds.forEach(id -> {
                List<TeamMember> newTeamMembers = teamMemberRepository.findByUserId(id)
                        .stream().filter(teamMember -> isTeamMemberNotAdded(entity, teamMember.getId())).toList();
                newTeamMembersIds.addAll(newTeamMembers.stream().map(TeamMember::getId).toList());
                List<Team> newTeams = newTeamMembers.stream().map(TeamMember::getTeam).toList();
                List<Project> newProjectsByUser = newTeams.stream().map(Team::getProject)
                        .filter(newProject -> isProjectNotAdded(currentProjectIds, newProject.getId())).toList();
                newProjects.addAll(newProjectsByUser);
            });
            if (!newProjects.isEmpty()) {
                entity.getProjects().addAll(newProjects);
            }
            if (!newTeamMembersIds.stream().distinct().toList().isEmpty()) {
                entity.getTeamMemberIds().addAll(newTeamMembersIds.stream().distinct().toList());
            }
        }
    }

    private void addNewProjects(MomentUpdateDto dto, Moment entity) {
        if (CollectionUtils.isNotEmpty(dto.getProjectIds())) {
            List<Long> currentProjectIds = entity.getProjects().stream().map(Project::getId).toList();
            List<Long> newProjectIdsFromDto = dto.getProjectIds().stream()
                    .filter(newProjectId -> isProjectNotAdded(currentProjectIds, newProjectId)).toList();
            List<Project> newProjects = projectRepository.findAllById(newProjectIdsFromDto);
            List<Long> teamMemberIds = entity.getTeamMemberIds();
            List<Team> newTeams = newProjects.stream().flatMap(project -> project.getTeams().stream()).toList();
            List<Long> newTeamMemberIds = newTeams.stream().flatMap(
                            team -> team.getTeamMembers().stream()
                                    .map(TeamMember::getId).
                                    filter(id -> !teamMemberIds.contains(id)))
                    .toList();
            if (!newProjects.isEmpty()) {
                entity.getProjects().addAll(newProjects);
            }
            if (!newTeamMemberIds.isEmpty()) {
                entity.getTeamMemberIds().addAll(newTeamMemberIds);
            }
        }
    }

    private static boolean isTeamMemberNotAdded(Moment entity, Long newTeamMemberId) {
        return !entity.getTeamMemberIds().contains(newTeamMemberId);
    }

    private static boolean isProjectNotAdded(List<Long> currentProjectIds, Long newProjectId) {
        return !currentProjectIds.contains(newProjectId);
    }
}
