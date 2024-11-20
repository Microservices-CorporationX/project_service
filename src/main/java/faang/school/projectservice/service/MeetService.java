package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.filter.meetFilters.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.MeetValidator;
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
    private final MeetValidator meetValidator;

    @Transactional
    public MeetDto createMeet(MeetDto meetDto) {
        log.info("Trying to create meet: {}", meetDto);
        meetValidator.validateUserExists(meetDto.creatorId());

        Meet meet = initializeMeet(meetDto);
        meet = meetRepository.save(meet);
        log.info("Meet success created");
        return meetMapper.toDto(meet);
    }

    @Transactional
    public MeetDto updateMeet(UpdateMeetDto updateMeetDto, HttpServletRequest request) {
        Meet meet = findMeetById(updateMeetDto.id());
        log.info("Trying to update meet from {} to {}", meet, updateMeetDto);
        meetValidator.validateMeetUpdating(meet);
        meetValidator.validateThatRequestWasSentByTheCreator(meet, request);

        meet.setTitle(updateMeetDto.title());
        meet.setDescription(updateMeetDto.description());
        meet.setStatus(updateMeetDto.status());
        meet.setUserIds(updateMeetDto.userIds());

        log.info("Meet success updated: {}", meet);
        return meetMapper.toDto(meet);
    }

    @Transactional
    public MeetDto cancelMeet(long id, HttpServletRequest request) {
        Meet meet = findMeetById(id);
        meetValidator.validateMeetUpdating(meet);
        meetValidator.validateThatRequestWasSentByTheCreator(meet, request);
        log.info("Trying to cancel meet: {}", meet);

        meet.setStatus(MeetStatus.CANCELLED);
        log.info("Meet success cancelled: {}", meet);
        return meetMapper.toDto(meet);
    }

    @Transactional
    public MeetDto deleteMeetingParticipant(long meetId, long userId, HttpServletRequest request) {
        Meet meet = findMeetById(meetId);
        log.info("Trying to delete meeting participant {} from: {}", userId, meet);
        meetValidator.validateMeetUpdating(meet);
        meetValidator.validateThatRequestWasSentByTheCreator(meet, request);
        meetValidator.validateParticipants(userId, meet);

        meet.getUserIds().remove(userId);
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
}
