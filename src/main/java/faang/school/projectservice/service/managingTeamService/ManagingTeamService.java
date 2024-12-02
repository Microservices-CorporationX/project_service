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


    public TeamMemberDto addTeamMember(Long projectId, TeamMemberDto TeamMemberDto, Long teamMemberId) {
        Project project = projectRepository.getProjectById(projectId);

        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new EntityNotFoundException("Cannot add member to a cancelled or completed project");
        }

        Team team = project.getTeams().stream()
                .filter(t -> t.getId().equals(teamMemberId))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Team with given ID not found in project"));

       TeamMember teamMemberInProject = team.getTeamMembers().stream().filter(teamMember -> teamMember.getId().equals(teamMemberId)).findFirst().orElseThrow(() ->
                new EntityNotFoundException("Team with given ID not found in project")
        );

       if (!teamMemberInProject.getRoles().contains(TeamRole.OWNER)) {
           throw new IllegalStateException("Only the owner can add members");
       }

        TeamMember existingMember = teamMemberJpaRepository.findByUserIdAndProjectId(TeamMemberDto.getUserId(), projectId);

        if (existingMember != null) {
            throw new IllegalStateException("User is already a member of the project");
        }

        TeamMember teamMember = teamMemberMapper.toEntity(TeamMemberDto);
        teamMember.setTeam(team);
        TeamMember savedMember = teamMemberJpaRepository.save(teamMember);

        return teamMemberMapper.toDto(savedMember);
    }

    public TeamMemberDto updateTeamMember(Long projectId, TeamMemberDto TeamMemberDto, Long teamMemberId, Long currentUserId) {
        Project project = projectRepository.getProjectById(projectId);
        checkProjectStatus(project);

        TeamMember existingMember = teamMemberJpaRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Team member with given ID not found in project"));

        if (!existingMember.getUserId().equals(currentUserId)) {
            validateUserIsOwner(currentUserId, projectId);
        }

        TeamMember teamMemberToUpdate = teamMemberMapper.toEntity(TeamMemberDto);
        teamMemberToUpdate.setId(teamMemberId);
        teamMemberToUpdate.setTeam(existingMember.getTeam());
        teamMemberToUpdate.setCreatedAt(existingMember.getCreatedAt());

        if (existingMember.getRoles().contains(TeamRole.OWNER)) {
            existingMember.setRoles(teamMemberToUpdate.getRoles());
            existingMember.setAccessLevel(teamMemberToUpdate.getAccessLevel());
        }

        existingMember.setNickname(teamMemberToUpdate.getNickname());
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
            throw new IllegalStateException("Cannot delete yourself");
        }

        validateUserIsOwner(currentUserId, projectId);

        teamMemberJpaRepository.delete(teamMember);
        return teamMemberMapper.toDto(teamMember);
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
        TeamMember teamMember = teamMemberJpaRepository.findById(teamMemberId).orElseThrow(() -> new EntityNotFoundException("Team member with given ID not found in project"));
        return teamMemberMapper.toDto(teamMember);
    }

    public List<TeamMemberDto> getTeamMemberWithFilter(Long projectId, TeamMemberFilterDto filters) {
        if (teamMemberFilters == null) {
           throw new IllegalStateException("Team member filters are not initialized");
        }
        Project project = projectRepository.getProjectById(projectId);
        Stream<TeamMember> teamMembers = project.getTeams().stream().flatMap(team -> team.getTeamMembers().stream());

        return teamMemberFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(teamMembers, filters))
                .map(teamMemberMapper::toDto)
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
            throw new IllegalStateException("Only the owner can delete members");
        }
    }
}
