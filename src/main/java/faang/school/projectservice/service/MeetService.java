package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.meetFilters.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final List<MeetFilter> filters;
    private final MeetMapper meetMapper;
    private final ProjectService projectService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Transactional
    public MeetDto createMeet(MeetDto meetDto) {
        log.info("Trying to create meet: {}", meetDto);
        validateUserExists(meetDto.creatorId());

        Meet meet = initializeMeet(meetDto);
        meet = meetRepository.save(meet);
        log.info("Meet success created");
        return meetMapper.toDto(meet);
    }

    @Transactional
    public MeetDto updateMeet(long id, UpdateMeetDto updateMeetDto) {
        Meet meet = findMeetById(id);
        log.info("Trying to update meet from {} to {}", meet, updateMeetDto);
        validateMeetUpdating(meet);
        validateThatRequestWasSentByTheCreator(meet);

        meetMapper.updateMeetFromDto(updateMeetDto, meet);

        log.info("Meet success updated: {}", meet);
        return meetMapper.toDto(meet);
    }

    @Transactional
    public MeetDto deleteMeetingParticipant(long meetId, long userId) {
        Meet meet = findMeetById(meetId);
        log.info("Trying to delete meeting participant {} from: {}", userId, meet);
        validateMeetUpdating(meet);
        validateThatRequestWasSentByTheCreator(meet);
        validateParticipants(userId, meet);

        meet.deleteParticipant(userId);
        log.info("Participants {} success deleted from meet {}", userId, meet);
        return meetMapper.toDto(meet);
    }

    public MeetDto getMeetById(long id) {
        return meetMapper.toDto(findMeetById(id));
    }

    @Transactional
    public List<MeetDto> getMeets(HttpServletRequest request) {
        log.info("Trying to get meets with filter:");
        Stream<Meet> meets = meetRepository.findAll().stream();
        List<Meet> meetList = filters.stream()
                .filter(filter -> filter.isApplicable(request))
                .reduce(meets, (stream, filter) -> filter.apply(stream, request),
                        ((subGenStream, stream) -> stream))
                .distinct()
                .toList();
        return meetMapper.toMeetDtoList(meetList);
    }

    @Transactional
    public List<MeetDto> getMeets() {
        List<Meet> meetList = meetRepository.findAll();
        return meetMapper.toMeetDtoList(meetList);
    }

    private Meet initializeMeet(MeetDto meetDto) {
        Meet meet = meetMapper.toMeet(meetDto);
        Project project = projectService.findProjectById(meetDto.projectId());
        meet.setProject(project);
        return meet;
    }

    public Meet findMeetById(long id) {
        return meetRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Meet with id %s does not exist".formatted(id)));
    }

    private void validateMeetUpdating(Meet meet) {
        if (meet.getStatus().equals(MeetStatus.CANCELLED) || meet.getStatus().equals(MeetStatus.COMPLETED)) {
            log.error("Trying to update cancelled or completed meet: {}", meet);
            throw new DataValidationException("Meet already cancelled or completed");
        }
    }

    private void validateThatRequestWasSentByTheCreator(Meet meet) {
        long requesterId = userContext.getUserId();
        validateUserExists(requesterId);

        if (meet.getCreatorId() != requesterId) {
            log.error("User with id {} is not the creator of the meeting {}", requesterId, meet);
            throw new DataValidationException("Only the team member who created the meet can update it");
        }
    }

    private void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with id %s does not exist".formatted(userId));
        }
    }

    private void validateParticipants(Long userId, Meet meet) {
        List<Long> participants = meet.getUserIds();
        if (!participants.contains(userId)) {
            throw new DataValidationException("User with id %s is not a participant in the meeting".formatted(userId));
        }
    }
}
