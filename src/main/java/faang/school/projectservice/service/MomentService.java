package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.filters.MomentFilter;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;


    public MomentDto create(MomentDto momentDto) {
        if (isExists(momentDto)) {
            throw new ValidationException("This moment already exists");
        }
        validateMoment(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        Moment savedMoment = momentRepository.save(moment);
        return (momentMapper.toDto(savedMoment));
    }

    public MomentDto update(MomentDto momentDto) {
        Moment moment = momentMapper.toEntity(momentDto);
        Moment updatedMoment = momentRepository.save(addAllUpdatesIfExists(moment));
        return (momentMapper.toDto(updatedMoment));
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filterDto) {
        List<Moment> allMoments = momentRepository.findAll();
        Stream<Moment> momentStream = allMoments.stream();
        return momentFilters.stream().filter(momentFilter -> momentFilter.isApplicable(filterDto))
                .reduce(momentStream, (stream, eventFilter) -> eventFilter.apply(stream, filterDto), (s1, s2) -> s2)
                .map(momentMapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments() {
        List<Moment> receivedMoments = momentRepository.findAll();
        return momentMapper.toDtoList(receivedMoments);
    }

    public MomentDto getMomentById(long id) {
        Optional<Moment> receivedMoment = momentRepository.findById(id);
        if (receivedMoment.isPresent()) {
            Moment foundMoment = receivedMoment.get();
            return momentMapper.toDto(foundMoment);
        }

        throw new ValidationException("There is no moment with given Id");
    }

    private boolean isExists(MomentDto momentDto) {
        List<Moment> presentMomentsList = momentRepository.findAll();
        List<MomentDto> presentMomentDtoList = momentMapper.toDtoList(presentMomentsList);
        return presentMomentDtoList.contains(momentDto);

    }

    private void validateMoment(MomentDto momentDto) {
        List<Long> relatedProjectIds = momentDto.getProjects().stream().map(project -> project.getId()).toList();
        List<Project> relatedProjects = projectRepository.findAllByIds(relatedProjectIds);
        if (relatedProjects.stream().anyMatch(project -> project.getStatus()
                .equals(ProjectStatus.COMPLETED))) {
            throw new ValidationException("The project was completed");
        }
        if (relatedProjects.stream().anyMatch(project -> project.getStatus()
                .equals(ProjectStatus.CANCELLED))) {
            throw new ValidationException("The project was cancelled");
        }
    }

    private Moment addAllUpdatesIfExists(Moment updateMoment) {
        Moment foundMomentFromDatabase = momentRepository.findById(updateMoment.getId())
                .orElseThrow(() -> new ValidationException("Such moment was not found!"));

        if (!updateMoment.getName().isBlank()) {
            foundMomentFromDatabase.setName(updateMoment.getName());
        }

        if (!updateMoment.getDescription().isBlank()) {
            foundMomentFromDatabase.setDescription(updateMoment.getDescription());
        }

        if (isProjectExists(updateMoment.getProjects())) {
            updateMoment.getProjects().removeIf(foundMomentFromDatabase.getProjects()::contains);
            if (!updateMoment.getProjects().isEmpty()) {
                foundMomentFromDatabase.getProjects().addAll(updateMoment.getProjects());
            }

        }

        if (isUserExists(updateMoment.getUserIds())) {
            updateMoment.getUserIds().removeIf(foundMomentFromDatabase.getUserIds()::contains);
            if (!updateMoment.getUserIds().isEmpty()) {
                foundMomentFromDatabase.getUserIds().addAll(updateMoment.getUserIds());
                List<TeamMember> tobeUpdatedUsers = teamMemberJpaRepository.findAllById(updateMoment.getUserIds());
                List<Project> toBeAddedProjectList = tobeUpdatedUsers.stream()
                        .map(user -> user.getTeam().getProject()).distinct().toList();
                foundMomentFromDatabase.getProjects().addAll(toBeAddedProjectList);
            }

        }

        return foundMomentFromDatabase;
    }

    private boolean isProjectExists(List<Project> project) {
        List<Project> allExistingProjects = projectRepository.findAll();
        return allExistingProjects.containsAll(project);
    }

    private boolean isUserExists(List<Long> userIds) {
        List<TeamMember> allExistingUsers = teamMemberJpaRepository.findAll();
        List<Long> allExistingUserIds = allExistingUsers.stream().map(user -> user.getUserId()).toList();
        return allExistingUserIds.containsAll(userIds);

    }
}
