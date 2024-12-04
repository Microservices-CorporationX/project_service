package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.MeetingAlreadyCancelledException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final ProjectService projectService;
    private final ProjectValidator projectValidator;
    private final TeamMemberService teamMemberService;
    private final List<Filter<Meet, MeetDto>> meetFilters;

    public MeetDto createMeeting(MeetDto createMeetDto) {
        projectValidator.validateProjectExistsById(createMeetDto.getProjectId());
        teamMemberService.validateInvitedUsersExistInTeam(createMeetDto);

        Meet meet = initializeMeet(createMeetDto);
        meet = meetRepository.save(meet);
        return meetMapper.toDto(meet);
    }

    public MeetDto updateMeeting(MeetDto updateMeetDto, long meetId) {
        Meet meeting = meetRepository.findById(meetId)
                .orElseThrow(EntityNotFoundException::new);

        projectValidator.validateProjectExistsById(updateMeetDto.getProjectId());

        validateMeetingUpdate(updateMeetDto, meetId, meeting);

        return meetMapper.toDto(meeting);
    }

    @Transactional
    public void deleteMeeting(long meetId, long creatorId) {
        validateMeetCreator(meetId, creatorId);

        meetRepository.deleteById(meetId);
        log.info("Meeting #{} successfully deleted by User #{}", meetId, creatorId);
    }


    @Transactional
    public List<MeetDto> filterMeetings(MeetDto filters) {
        if (filters == null || meetFilters.stream().noneMatch(filter -> filter.isApplicable(filters))) {
            return meetRepository.findAll().stream()
                    .map(meetMapper::toDto)
                    .toList();
        }

        Stream<Meet> meet = meetRepository.findAll().stream();

        Stream<Meet> filterMeet = meetFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(meet, (currentStream, filter) -> filter.apply(currentStream, filters), (s1, s2) -> s1);

        return filterMeet.map(meetMapper::toDto).toList();
    }

    public List<MeetDto> getAllMeetings() {
        return meetRepository.findAll().stream().map(meetMapper::toDto).toList();
    }


    public MeetDto getMeetingById(long meetId) {
        return meetRepository.findById(meetId).map(meetMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Meet initializeMeet(MeetDto meetDto) {
        Meet meet = meetMapper.toMeet(meetDto);
        Project project = projectService.getProjectById(meetDto.getProjectId());
        meet.setProject(project);

        return meet;
    }

    private void validateMeetCreator(long meetId, long creatorId) {
        Meet meet = meetRepository.findById(meetId).orElseThrow(EntityNotFoundException::new);

        if (!(creatorId == meet.getCreatorId())) {
            throw new UnauthorizedAccessException("User is not allowed to delete this meeting");
        }
    }

    private void validateMeetingUpdate(MeetDto updateMeetDto, long meetId, Meet meeting) {
        if (meeting.getStatus().equals(MeetStatus.CANCELLED) && updateMeetDto.getMeetStatus().equals(MeetStatus.CANCELLED)) {
            throw new MeetingAlreadyCancelledException("Meeting is already cancelled");
        } else {
            log.info("Meeting with ID {} is updated", meetId);
            meeting.setStatus(updateMeetDto.getMeetStatus());
            meeting.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        }

        if (!(meeting.getCreatorId() == updateMeetDto.getCreatorId())) {
            throw new UnauthorizedAccessException("Unauthorized access to meet with ID " + meetId);
        }
    }
}
