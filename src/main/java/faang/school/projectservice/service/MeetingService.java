package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.MeetingValidator;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final MeetingValidator meetingValidator;
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

        meetingValidator.validateMeetingUpdate(updateMeetDto, meetId, meeting);

        return meetMapper.toDto(meeting);
    }

    public void deleteMeeting(Long projectId, Long currentUserId, long meetId) {
        projectValidator.validateProjectExistsById(projectId);

        Project project = projectService.getProjectById(projectId);

        projectValidator.checkUserIsProjectOwner(currentUserId, project);

        meetRepository.deleteById(meetId);
        log.info("Meeting #{} successfully deleted by User #{}", meetId, currentUserId);
    }

    public List<MeetDto> filterMeetings(MeetDto filters) {
        Stream<Meet> meet = meetRepository.findAll().stream();

        Stream<Meet> filterMeet = meetFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(meet, (currentStream, filter) -> filter.apply(currentStream, filters), (s1, s2) -> s1);

        return filterMeet.map(meetMapper::toDto).toList();
    }

    public List<MeetDto> getAllMeetings(Long projectId) {
        return meetRepository.findById(projectId).stream().map(meetMapper::toDto).toList();
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
}
