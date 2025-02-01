package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;
    private final MomentServiceValidator momentServiceValidator;

    @Override
    public MomentResponseDto createMoment(MomentCreateRequestDto momentCreateRequestDto) {
        momentServiceValidator.validateMomentName(momentCreateRequestDto.name());
        Moment momentSaved = momentRepository.save(momentMapper.toMomentEntity(momentCreateRequestDto));
        MomentResponseDto createdMomentResponseDto = momentMapper.toMomentResponseDto(momentSaved);
        log.info("Created moment {}", createdMomentResponseDto);
        return createdMomentResponseDto;
    }

    @Override
    public MomentResponseDto updateMoment(Long momentId, MomentUpdateRequestDto momentUpdateRequestDto) {
        momentServiceValidator.validateMomentName(momentUpdateRequestDto.name());

        MomentResponseDto initialMomentDto = getMoment(momentId);
        MomentUpdateRequestDto updatedMomentDto = updateMomentData(initialMomentDto, momentUpdateRequestDto);

        Moment momentSaved = momentRepository.save(momentMapper.toMomentEntity(updatedMomentDto));
        MomentResponseDto momentSavedDto = momentMapper.toMomentResponseDto(momentSaved);
        log.info("Updated moment {}", momentSavedDto);
        return momentSavedDto;
    }

    @Override
    public List<MomentResponseDto> getMoments(MomentFilterDto filter) {
        List<Moment> moments = momentRepository.findAll();
        return getFilteredMoments(moments.stream(), filter);
    }

    @Override
    public List<MomentResponseDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        return momentMapper.toMomentResponseDtos(moments);
    }

    @Override
    public MomentResponseDto getMoment(Long id) {
        Optional<Moment> optionalMoment = momentRepository.findById(id);
        Moment moment = optionalMoment.orElseThrow();
        return momentMapper.toMomentResponseDto(moment);
    }

    private List<Long> getAllProjectsTeamMemberIds(Moment moment) {
        return moment.getProjects().stream()
                .filter(project -> project.getStatus() == ProjectStatus.IN_PROGRESS)
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .sorted()
                .toList();
    }

    private MomentUpdateRequestDto updateMomentData(MomentResponseDto initialMomentDto, MomentUpdateRequestDto updatedMomentDto) {
        List<Long> allTeamMembersIds = getAllMomentTeamMembersIds(initialMomentDto, updatedMomentDto);
        List<Long> allProjectsIds = getAllMomentProjects(initialMomentDto, updatedMomentDto);

        return MomentUpdateRequestDto.builder()
                .teamMemberToAddIds(allTeamMembersIds)
                .projectToAddIds(allProjectsIds)
                .name(updatedMomentDto.name())
                .description(updatedMomentDto.description())
                .build();
    }

    private List<Long> getAllMomentTeamMembersIds(MomentResponseDto initialMomentDto,
                                                  MomentUpdateRequestDto updatedMomentDto) {
        List<Long> initialAllProjectTeamMembersIds
                = getAllProjectsTeamMemberIds(momentMapper.toMomentEntity(initialMomentDto));
        List<Long> addedProjectTeamMembersIds
                = getAllProjectsTeamMemberIds(momentMapper.toMomentEntity(updatedMomentDto));
        List<Long> initialTeamMembersIds = initialMomentDto.teamMembersIds();
        List<Long> addedTeamMembersIds = updatedMomentDto.teamMemberToAddIds();
        List<Long> resultTeamMemberIds = new ArrayList<>();

        resultTeamMemberIds.addAll(initialTeamMembersIds);
        resultTeamMemberIds.addAll(initialAllProjectTeamMembersIds);
        resultTeamMemberIds.addAll(addedProjectTeamMembersIds);
        resultTeamMemberIds.addAll(addedTeamMembersIds);

        return resultTeamMemberIds.stream()
                .distinct()
                .sorted()
                .toList();
    }

    private List<Long> getAllMomentProjects(MomentResponseDto initialMomentDto,
                                            MomentUpdateRequestDto updatedMomentDto) {
        List<Long> initialAllProjects = initialMomentDto.projectIds();
        List<Long> addedProjects = updatedMomentDto.projectToAddIds();
        List<Long> resultProjects = new ArrayList<>();

        resultProjects.addAll(initialAllProjects);
        resultProjects.addAll(addedProjects);

        return resultProjects.stream()
                .distinct()
                .sorted()
                .toList();
    }

    private List<MomentResponseDto> getFilteredMoments(Stream<Moment> moments, MomentFilterDto momentFilterDto) {
        for (MomentFilter momentFilter : momentFilters) {
            if (momentFilter.isApplicable(momentFilterDto)) {
                moments = momentFilter.apply(moments, momentFilterDto);
            }
        }
        return moments
                .map(momentMapper::toMomentResponseDto)
                .toList();
    }
}