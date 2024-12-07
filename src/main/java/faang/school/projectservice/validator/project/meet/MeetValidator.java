package faang.school.projectservice.validator.project.meet;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.service.project.ProjectService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeetValidator {
    private final UserServiceClient userServiceClient;

    public void validate(MeetDto meetDto, long userId, ProjectService projectService) {
        validation(meetDto.getProjectId(), userId, projectService);

        if (meetDto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("start date time must be after now date");
        }

        if (meetDto.getEndDateTime().isBefore(meetDto.getStartDateTime())) {
            throw new IllegalStateException("start date time must be after end date");
        }
    }

    public void validate(long projectId, long userId, ProjectService projectService) {
        validation(projectId, userId, projectService);
    }

    private void validation(long projectId, long userId, ProjectService projectService) {
        try {
            userServiceClient.getUser(userId);
        } catch(FeignException e) {
            throw new IllegalStateException(String.format("user with id %d not found", userId), e);
        }

        if (!projectService.hasUserInProject(projectId, userId)) {
            throw new IllegalStateException(
                    String.format("user with id %d not found in project with id %d", userId, projectId));
        }
    }
}
