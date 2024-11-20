package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final MeetMapper meetMapper;
    private final MeetValidator meetValidator;
    private final List<MeetFilter> meetFilters;

    public MeetResponseDto createMeet(long creatorId, MeetRequestDto meetRequestDto) {
        validateProject(meetRequestDto.getProjectId());
        var meet = meetMapper.toEntity(meetRequestDto);
        var project = projectRepository.findById(meetRequestDto.getProjectId());
        meet.setCreatorId(creatorId);
        meet.setProject(project);
        log.info("Meet created: {}", meet.getId());
        return meetMapper.toDto(meetRepository.save(meet));
    }

    public MeetResponseDto updateMeet(long creatorId, MeetRequestDto meetRequestDto) {
        var meetToUpdate = findMeetById(meetRequestDto.getId());
        meetValidator.validateMeetToUpdate(meetToUpdate, creatorId);
        var meetsByCreatorId = findMeetsByCreatorId(creatorId);
        meetToUpdate.setUpdatedAt(LocalDateTime.now());
        if (meetRequestDto.getStatus() == MeetStatus.CANCELLED) {
            meetToUpdate.setStatus(MeetStatus.CANCELLED);
        }
        meetMapper.update(meetToUpdate, meetRequestDto);
        meetsByCreatorId.add(meetToUpdate);
        meetRepository.save(meetToUpdate);
        log.info("Meet '{}' updated successfully with status '{}'",
                meetToUpdate.getId(),
                meetToUpdate.getStatus());
        return meetMapper.toDto(meetToUpdate);
    }

    public void deleteMeet(long creatorId, Long id) {
        var meet = findMeetById(id);
        meetValidator.validateMeetToDelete(meet, creatorId);
        log.info("Meet removed: {}", meet.getId());
        meetRepository.delete(meet);
    }

    public List<MeetResponseDto> findAllByProjectIdFilter(Long projectId, MeetFilterDto filter) {
        var project = projectRepository.getProjectById(projectId);
        var meets = project.getMeets().stream();
        log.info("Found {} filtered stages for project: {}", meets.count(), projectId);
        return meetFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .reduce(meets, (stream, f) -> f.apply(stream, filter), (s1, s2) -> s1)
                .map(meetMapper::toDto)
                .toList();
    }

    public List<MeetResponseDto> findByFilter(MeetFilterDto filter) {
        var meets = meetRepository.findAll().stream();
        log.info("Applying filters to meets. Filter params: title pattern - {}, created at - {}",
                filter.titlePattern(), filter.createdAt());

        return meetFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .reduce(meets, (stream, f) -> f.apply(stream, filter), (s1, s2) -> s1)
                .map(meetMapper::toDto)
                .toList();
    }

    public MeetResponseDto findById(Long id) {
        var meet = findMeetById(id);
        return meetMapper.toDto(meet);
    }

    private List<Meet> findMeetsByCreatorId(long creatorId) {
        return meetRepository.findByCreatorId(creatorId);
    }

    private Meet findMeetById(Long id) {
        return meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meet with ID: %d was not found".formatted(id)));
    }

    private void validateProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }
}