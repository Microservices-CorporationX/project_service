package faang.school.projectservice.service.managingTeamService;

import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.managingTeamMapper.managingTeamMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.managingFilter.managingTeamFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class managingTeamService {

    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final managingTeamMapper teamMemberMapper;
    private final List<managingTeamFilter> teamMemberFilters;


    public TeamMemberDto addTeamMember(Long projectId, TeamMemberDto TeamMemberDto, @PathVariable Long teamId) {
        Project project = projectRepository.getProjectById(projectId);

        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot add member to a cancelled or completed project");
        }

        Team team = project.getTeams().stream()
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Team with given ID not found in project"));

        TeamMember existingMember = teamMemberJpaRepository.findByUserIdAndProjectId(TeamMemberDto.getUserId(), projectId);

        if (existingMember != null) {
            throw new IllegalStateException("User is already a member of the project");
        }

        TeamMember teamMember = teamMemberMapper.toEntity(TeamMemberDto);
        teamMember.setTeam(team);
        TeamMember savedMember = teamMemberJpaRepository.save(teamMember);

        return teamMemberMapper.toDto(savedMember);
    }

    public TeamMemberDto updateTeamMember(Long projectId, TeamMemberDto TeamMemberDto, @PathVariable Long teamMemberId, Long currentUserId) {
        checkProjectStatusAndThrowException(projectId);

        TeamMember existingMember = teamMemberJpaRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalStateException("Team member with given ID not found in project"));

        if (!existingMember.getUserId().equals(currentUserId)) {
            boolean isTeamLead = existingMember.getRoles().contains(TeamRole.OWNER);
            if (!isTeamLead) {
                throw new IllegalStateException("Only the team lead can update roles and access level");
            }
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

    public TeamMemberDto deleteTeamMember(Long projectId, @PathVariable Long teamMemberId, Long currentUserId) {
        checkProjectStatusAndThrowException(projectId);

        TeamMember teamMember = teamMemberJpaRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalStateException("Team member with given ID not found in project"));

        TeamMember currentUser = teamMemberJpaRepository.findByUserId(currentUserId).stream().findFirst().orElse(null);


        boolean isTeamLeadOrOwner = currentUser.getRoles().contains(TeamRole.OWNER);
        if (!isTeamLeadOrOwner) {
            throw new IllegalStateException("Only the team lead or owner can delete members");
        }

        teamMemberJpaRepository.delete(teamMember);
        return teamMemberMapper.toDto(teamMember);
    }

    public List<TeamMemberDto> getAllMembers(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot get members of a cancelled or completed project");
        }

        List<TeamMember> teamMembers = teamMemberJpaRepository.findAllByProjectId(projectId);

        List<TeamMemberDto> TeamMemberDto = teamMembers.stream()
                .map(teamMemberMapper::toDto)
                .toList();

        return TeamMemberDto;
    }

    public TeamMemberDto getTeamMember(Long projectId, Long teamMemberId) {
        TeamMember teamMember = teamMemberJpaRepository.findById(teamMemberId).orElse(null);
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

    public void checkProjectStatusAndThrowException(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot add member to a cancelled or completed project");
        }
    }
}
