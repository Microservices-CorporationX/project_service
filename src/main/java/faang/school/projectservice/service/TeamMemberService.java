package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final TeamRepository teamRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final ProjectJpaRepository projectJpaRepository;


    public TeamMemberDto addTeamMemberInTeam(TeamMemberDto teamMemberDto, List<ResourceType> allowedResourceTypes) {
        UserDto userDto;
        TeamMember authorizedTeamMember;
        TeamMember foundTeamMember;


        userDto = getUserById(userContext.getUserId());
        authorizedTeamMember = getTeamMemberById(userDto.getId());
        if (authorizedTeamMember.getRoles().contains(TeamRole.OWNER)
                || authorizedTeamMember.getRoles().contains(TeamRole.MANAGER)) {
            Team toBeAddedTeam = getTeamById(teamMemberDto.getTeamId());
            if (toBeAddedTeam.getTeamMembers().stream().noneMatch(teamMember -> teamMember
                    .getUserId().equals(teamMemberDto.getUserId())) && toBeAddedTeam.getProject().getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream()).
                    noneMatch(teamMember -> teamMember.getUserId().equals(teamMemberDto.getUserId()))) {
                foundTeamMember = getTeamMemberById(teamMemberDto.getId());
                foundTeamMember.setTeam(toBeAddedTeam);
                teamRepository.save(foundTeamMember.getTeam());
            } else {
                throw new ValidationException("This user already exists in this project!");
            }
            if (!teamMemberDto.getRoles().isEmpty() && !foundTeamMember.getRoles().containsAll(teamMemberDto.getRoles())) {

                List<TeamRole> toBeAddedRoles = teamMemberDto.getRoles().stream()
                        .filter(role -> !foundTeamMember.getRoles().contains(role)).toList();
                foundTeamMember.getRoles().addAll(toBeAddedRoles);
                teamMemberJpaRepository.save(foundTeamMember);
            }
            if (!allowedResourceTypes.isEmpty()) {
                List<Resource> toBeAllowedResourceList = foundTeamMember.getTeam().getProject().getResources().stream()
                        .filter(resource -> allowedResourceTypes.contains(resource.getType())).toList();
                if (!toBeAllowedResourceList.isEmpty()) {
                    List<Resource> allowedResourceList = toBeAllowedResourceList.stream()
                            .peek(resource -> resource.getAllowedRoles().addAll(foundTeamMember.getRoles())).toList();
                    resourceRepository.saveAll(allowedResourceList);
                }
            }
        } else {
            throw new ValidationException("You don't have the right to add a team member!");
        }

        return teamMemberMapper.toDto(foundTeamMember);
    }

    public TeamMemberDto addTeamRole(Long teamMemberId, TeamRole role) {
        UserDto userDto = getUserById(userContext.getUserId());
        TeamMember authorizedTeamMember = getTeamMemberById(userDto.getId());
        if (authorizedTeamMember.getRoles().contains(TeamRole.MANAGER)) {
            TeamMember foundTeamMember = getTeamMemberById(teamMemberId);
            if (!foundTeamMember.getRoles().contains(role)) {
                foundTeamMember.getRoles().add(role);
                teamMemberJpaRepository.save(foundTeamMember);
                TeamMemberDto updatedTeamMemberDto = teamMemberMapper.toDto(foundTeamMember);
                updatedTeamMemberDto.setUpdatedAt(LocalDateTime.now());
                return updatedTeamMemberDto;
            } else {
                throw new ValidationException("The team member already has this role!");
            }
        } else {
            throw new ValidationException("You don't have authorization to add the role!");
        }
    }

    public TeamMemberDto removeTeamRole(Long teamMemberId, TeamRole role) {
        UserDto userDto = getUserById(userContext.getUserId());
        TeamMember authorizedTeamMember = getTeamMemberById(userDto.getId());
        if (authorizedTeamMember.getRoles().contains(TeamRole.MANAGER)) {
            TeamMember foundTeamMember = getTeamMemberById(teamMemberId);
            if (foundTeamMember.getRoles().contains(role)) {
                foundTeamMember.getRoles().remove(role);
                teamMemberJpaRepository.save(foundTeamMember);
                TeamMemberDto updatedTeamMemberDto = teamMemberMapper.toDto(foundTeamMember);
                updatedTeamMemberDto.setUpdatedAt(LocalDateTime.now());
                return updatedTeamMemberDto;
            } else {
                throw new ValidationException("The team member doesn't have this role!");
            }
        } else {
            throw new ValidationException("You don't have authorization to remove the role!");
        }

    }

    public void removeTeamMember(Long teamMemberId, Long projectId) {
        Project receivedProject = projectJpaRepository.getReferenceById(projectId);
        UserDto authorizedPerson = getUserById(userContext.getUserId());
        if (authorizedPerson.getId().equals(receivedProject.getOwnerId())) {
            TeamMember receivedTeamMember = getTeamMemberById(teamMemberId);
            if (receivedProject.getTeams().stream().flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(teamMember -> teamMember.getId().equals(receivedTeamMember.getId()))) {
                teamMemberJpaRepository.deleteById(teamMemberId);
            } else {
                throw new ValidationException("The project doesn't contain the team member!");
            }
        } else {
            throw new ValidationException("You don't have authorization to remove the team member!");

        }
    }

    public List<TeamMemberDto> getAllTeamMembers() {
        List<TeamMember> allTeamMembers = teamMemberJpaRepository.findAll();
        return teamMemberMapper.toDtoList(allTeamMembers);

    }

    public TeamMemberDto getMemberById(Long id) {
        TeamMember receivedTeamMember = getTeamMemberById(id);
        return teamMemberMapper.toDto(receivedTeamMember);
    }

    public List<TeamMemberDto> getTeamMembersByProjectName(Long projectId, TeamRole role) {
        Optional<Project> project = projectJpaRepository.findById(projectId);
        if (project.isPresent()) {
            Project receivedProject = project.get();
            List<TeamMember> teamMemberList = receivedProject.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(teamMember -> teamMember.getRoles().contains(role)).toList();
            if (teamMemberList.isEmpty()) {
                throw new ValidationException("The project doesn't contain a team member with this role!");
            }
            return teamMemberMapper.toDtoList(teamMemberList);
        } else {
            throw new ValidationException("A project with this Id doesn't exist!");
        }
    }

    private TeamMember getTeamMemberById(Long id) {
        TeamMember foundTeamMember;

        try {
            foundTeamMember = teamMemberRepository.findById(id);
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new ValidationException(entityNotFoundException.getMessage());
        }
        return foundTeamMember;
    }

    private Team getTeamById(Long id) {
        Optional<Team> receivedTeam = teamRepository.findById(id);
        if (receivedTeam.isPresent()) {
            return receivedTeam.get();
        } else throw new ValidationException("The team was not found!");
    }

    private UserDto getUserById(Long userId) {
        UserDto userDto;
        try {
            userDto = userServiceClient.getUser(userId);
            return userDto;
        } catch (FeignException.FeignClientException feignClientException) {
            throw new ValidationException(feignClientException.getMessage());
        }
    }

}
