package faang.school.projectservice.service.managingTeamService;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.managingTeamMapper.ManagingTeamMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.managingFilter.ManagingTeamFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ManagingTeamService {

    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final ManagingTeamMapper teamMemberMapper;
    private final List<ManagingTeamFilter> teamMemberFilters;


    public TeamMemberDto addTeamMember(Long projectId, TeamMemberDto teamMemberDto, Long teamMemberId) {
        Project project = projectRepository.getProjectById(projectId);
        checkProjectStatus(project);

        Team team = project.getTeams().stream()
                .filter(t -> t.getId().equals(teamMemberId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team with given ID not found in project"));

        TeamMember teamMemberInProject = team.getTeamMembers().stream()
                .filter(teamMember -> teamMember.getId().equals(teamMemberId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team member with given ID not found in project"));

        if (!teamMemberInProject.getRoles().contains(TeamRole.OWNER)) {
            throw new IllegalStateException("Only the owner can add members");
        }

        TeamMember existingMember = teamMemberJpaRepository.findByUserIdAndProjectId(teamMemberDto.getUserId(), projectId);

        if (existingMember != null) {
            throw new IllegalStateException("User is already a member of the project");
        }

        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDto);
        teamMember.setTeam(team);
        TeamMember savedMember = teamMemberJpaRepository.save(teamMember);

        return teamMemberMapper.toDto(savedMember);
    }

    public TeamMemberDto updateTeamMember(Long projectId, TeamMemberDto teamMemberDto, Long teamMemberId, Long currentUserId) {
        Project project = projectRepository.getProjectById(projectId);
        checkProjectStatus(project);

        TeamMember existingMember = teamMemberJpaRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Team member with given ID not found in project"));

        if (!existingMember.getUserId().equals(currentUserId)) {
            validateUserIsOwner(currentUserId, projectId);
        }

        TeamMember teamMemberToUpdate = teamMemberMapper.toEntity(teamMemberDto);
        teamMemberToUpdate.setId(existingMember.getId());
        teamMemberToUpdate.setUserId(existingMember.getUserId());
        teamMemberToUpdate.setTeam(existingMember.getTeam());
        teamMemberToUpdate.setCreatedAt(existingMember.getCreatedAt());

        if (existingMember.getRoles().contains(TeamRole.OWNER)) {
            existingMember.setRoles(teamMemberToUpdate.getRoles());
            existingMember.setAccessLevel(teamMemberToUpdate.getAccessLevel());
        }

        existingMember.setName(teamMemberToUpdate.getName());
        existingMember.setDescription(teamMemberToUpdate.getDescription());

        TeamMember savedMember = teamMemberJpaRepository.save(existingMember);

        return teamMemberMapper.toDto(savedMember);
    }

    public TeamMemberDto deleteTeamMember(Long projectId, Long teamMemberId, Long currentUserId) {
        Project project = projectRepository.getProjectById(projectId);
        checkProjectStatus(project);

        TeamMember teamMember = teamMemberJpaRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Team member with given ID not found in project"));

        if (!teamMember.getUserId().equals(currentUserId)) {
            validateUserIsOwner(currentUserId, projectId);
        }

        TeamMemberDto dto = teamMemberMapper.toDto(teamMember);

        teamMemberJpaRepository.delete(teamMember);
        return dto;
    }

    public List<TeamMemberDto> getAllMembers(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        checkProjectStatus(project);

        List<TeamMember> teamMembers = teamMemberJpaRepository.findAllByProjectId(projectId);
        List<TeamMemberDto> TeamMemberDto = teamMembers.stream()
                .map(teamMemberMapper::toDto)
                .toList();

        return TeamMemberDto;
    }

    public TeamMemberDto getTeamMember(Long projectId, Long teamMemberId) {
        Project project = projectRepository.getProjectById(projectId);

        checkProjectStatus(project);

        TeamMember teamMember = teamMemberJpaRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Team member with given ID not found in project"));

        if (!teamMember.getTeam().getProject().getId().equals(projectId)) {
            throw new EntityNotFoundException("Team member with ID " + teamMemberId + " is not part of the project with ID " + projectId);
        }

        return teamMemberMapper.toDto(teamMember);
    }

    public List<TeamMemberDto> getTeamMemberWithFilter(Long projectId, TeamMemberFilterDto filters) {
        if (teamMemberFilters == null) {
            throw new IllegalStateException("Team member filters are not initialized");
        }
        List<TeamMember> teamMembers = teamMemberJpaRepository.findAllByProjectIdWithRoles(projectId, filters.getRole());
        Stream<TeamMember> teamMemberStream = teamMembers.stream();

        for (ManagingTeamFilter filter : teamMemberFilters) {
            if (filter.isApplicable(filters)) {
                teamMemberStream = filter.apply(teamMemberStream, filters);
            }
        }
        return teamMemberStream.map(teamMemberMapper::toDto)
                .toList();
    }

    private void checkProjectStatus(Project project) {
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot add member to a cancelled or completed project");
        }
    }

    private void validateUserIsOwner(Long userId, Long projectId) {
        TeamMember currentUser = teamMemberJpaRepository.findByUserId(userId).stream()
                .filter(member -> member.getTeam().getId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User is not part of the project"));

        if (!currentUser.getRoles().contains(TeamRole.OWNER)) {
            throw new IllegalStateException("Only the owner can change or delete members");
        }
    }
}
