package faang.school.projectservice.service.meet;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.ExternalServiceException;
import faang.school.projectservice.exception.TeamMemberValidationException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.teammember.TeamMemberService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberService teamMemberService;
    private final UserServiceClient userServiceClient;
    private final MeetMapper meetMapper;

    public MeetDto createMeeting(MeetDto meetDto) {
        Long projectId = meetDto.getProjectId();
        Long creatorId = meetDto.getCreatorId();

        Project project = projectRepository.getProjectById(meetDto.getProjectId());

        checkUserExistence(Collections.singletonList(meetDto.getCreatorId()));
        checkUserExistence(meetDto.getUserIds());

        if (!teamMemberService.isUserInAnyTeamOfProject(projectId, creatorId)) {
            throw new UnauthorizedAccessException("Only a team member of the project can create a meeting");
        }

        if (!teamMemberService.areAllUsersInProjectTeams(projectId, meetDto.getUserIds())) {
            throw new TeamMemberValidationException("Not all users are team members of the project with ID: " + projectId);
        }

        Meet meetToSave = meetMapper.toEntity(meetDto);
        meetToSave.setStatus(MeetStatus.PENDING);
        meetToSave.setProject(project);

        Meet savedMeet = meetRepository.save(meetToSave);

        log.info("Meeting was created with ID: {}", savedMeet.getId());
        return meetMapper.toDto(savedMeet);
    }

    public MeetDto updateMeeting(Long meetId, Long userId, MeetDto meetDto) {
        meetDto.setId(meetId);

        Meet meetToUpdate = meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException("The meeting not found with ID: " + meetId));

        if (!userId.equals(meetToUpdate.getCreatorId())) {
            throw new UnauthorizedAccessException("Only the creator can update the meeting");
        }

        checkUserExistence(meetDto.getUserIds());
        if (!teamMemberService.areAllUsersInProjectTeams(meetToUpdate.getProject().getId(), meetDto.getUserIds())) {
            throw new TeamMemberValidationException("Not all users are team members of the project with ID: " + meetToUpdate.getProject().getId());
        }

        meetMapper.updateEntity(meetDto, meetToUpdate);
        Meet updatedMeet = meetRepository.save(meetToUpdate);
        log.info("The meeting with ID: {} was updated successfully at {}", meetToUpdate.getId(), updatedMeet.getUpdatedAt());
        return meetMapper.toDto(updatedMeet);
    }

    public void deleteMeeting(Long meetId, Long userId) {
        Meet meetToDelete = meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException("The meeting not found with ID: " + meetId));

        if (!userId.equals(meetToDelete.getCreatorId())) {
            throw new UnauthorizedAccessException("Only the creator can delete the meeting");
        }

        meetRepository.deleteById(meetId);
        log.info("The meeting with ID: {} was deleted by the user with ID: {}", meetId, userId);
    }

    //TODO: think about date of a meeting
    public List<MeetDto> getMeetingsByProjectFilteredByDateOrTitle(Long projectId, String title, LocalDateTime date) {
        List<Meet> meets;
        if (title != null && date != null) {
            meets = meetRepository.findByProjectIdAndTitleContainingIgnoreCaseAndCreatedAtAfter(projectId, title, date);
        } else if (title != null) {
            meets = meetRepository.findByProjectIdAndTitleContainingIgnoreCase(projectId, title);
        } else if (date != null) {
            meets = meetRepository.findByProjectIdAndCreatedAtAfter(projectId, date);
        } else {
            meets = meetRepository.findByProjectId(projectId);
        }
        log.info("Fetch meetings of the project with ID: {} and filtered by title: {} or date: {}", projectId, title, date);
        return meetMapper.toDto(meets);
    }

    public List<MeetDto> getAllMeetings() {
        List<Meet> meets = meetRepository.findAll();
        log.info("Fetch all meetings");
        return meetMapper.toDto(meets);
    }

    public MeetDto getMeetingById(Long meetId) {
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException("The meeting not found with ID: " + meetId));
        log.info("Fetch the meeting with ID: {}", meetId);
        return meetMapper.toDto(meet);
    }

    private void checkUserExistence(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        try {
            List<UserDto> users;
            if (userIds.size() == 1) {
                UserDto user = userServiceClient.getUser(userIds.iterator().next());
                users = user != null ? List.of(user) : null;
            } else {
                users = userServiceClient.getUsersByIds(new ArrayList<>(userIds));
            }

            if (users == null || users.isEmpty() || users.size() != userIds.size()) {
                throw new EntityNotFoundException("User(s) not found with IDs: " + userIds);
            }
        } catch (FeignException.NotFound e) {
            log.warn("User(s) not found with IDs: {}", userIds, e);
            throw new EntityNotFoundException("User Service returned 404 - User(s) not found with IDs: " + userIds);
        } catch (FeignException e) {
            log.error("Error while communicating with User Service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Failed to communicate with User Service. Please try again later.");
        }
    }
}
