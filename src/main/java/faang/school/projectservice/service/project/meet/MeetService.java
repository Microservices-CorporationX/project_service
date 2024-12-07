package faang.school.projectservice.service.project.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.project.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.project.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetValidator meetValidator;
    private final ProjectService projectService;
    private final UserContext userContext;
    private final MeetMapper meetMapper;
    private final List<Filter<MeetFilterDto, Stream<Meet>>> meetFilters;

    public MeetDto createMeet(MeetDto meetDto) {
        long userId = userContext.getUserId();
        meetValidator.validate(meetDto, userId, projectService);

        Meet meet = meetMapper.toEntity(meetDto);
        meet.setStatus(MeetStatus.PENDING);
        meet.setCreatorId(userId);
        meet.setProject(projectService.getProjectEntityById(meetDto.getProjectId()));

        return meetMapper.toDto(meetRepository.save(meet));
    }

    public MeetDto updateMeet(MeetDto meetDto) {
        long userId = userContext.getUserId();
        meetValidator.validate(meetDto, userId, projectService);

        Meet meet = meetRepository.findByIdAndCreatorId(meetDto.getId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Meet not found or user not creator"));

        meetMapper.update(meetDto, meet);

        return meetMapper.toDto(meetRepository.save(meet));
    }

    public List<MeetDto> getMeets(long projectId, MeetFilterDto meetFilterDto) {
        long userId = userContext.getUserId();
        meetValidator.validate(projectId, userId, projectService);

        List<Meet> meets = meetRepository.findAllByProjectId(projectId);

        return meetFilters.stream()
                .filter(filter -> filter.isApplicable(meetFilterDto))
                .reduce(meets.stream(),
                        (stream, filter) -> filter.apply(meetFilterDto, stream),
                        (s1, s2) -> s1)
                .map(meetMapper::toDto)
                .toList();
    }

    public MeetDto getMeetByIdAndUserId(long id) {
        long userId = userContext.getUserId();

        return meetMapper.toDto(meetRepository.findByIdAndCreatorId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Meet not found or user not creator")));
    }
}
