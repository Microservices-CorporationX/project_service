package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.service.MomentService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.time.LocalDateTime.parse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH);

    @Override
    public MomentResponseDto createMoment(MomentRequestDto momentRequestDto) {
        validateMoment(momentRequestDto);
        Moment moment = momentRepository.save(momentMapper.toMomentEntity(momentRequestDto));
        MomentResponseDto createdMomentResponseDto = momentMapper.toMomentResponseDto(moment);
        log.info("Created moment {}", createdMomentResponseDto);
        return createdMomentResponseDto;
    }

    @Override
    public MomentResponseDto updateMoment(MomentRequestDto momentRequestDto) {
        if (momentRequestDto.id() != null) {
            log.info("Updated moment : {}", momentRequestDto);
            return createMoment(momentRequestDto);
        } else {
            log.error("Unable update moment, because it's Id is null {}", momentRequestDto);
            throw new IllegalArgumentException("Unable update moment, because it's Id is null");
        }
    }

    @Override
    public List<MomentResponseDto> getMoments(MomentFilterDto filter) {
        List<Moment> moments = momentRepository.findAll();
        return momentMapper.toMomentResponseDtos(moments);
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

    private void addProject(Moment moment, Project project) {
        List<Project> projects = moment.getProjects();
        projects.add(project);
        moment.setProjects(projects);
        List<Long> teamMemberIds = getAllTeamMemberIds(moment);
        moment.setUserIds(teamMemberIds);

    }

    private void addTeamMember(Moment moment, TeamMember teamMember) {
        List<Long> teamMemberIds = moment.getUserIds();
        teamMemberIds.add(teamMember.getId());
        moment.setUserIds(teamMemberIds);
        Project teamMemberProject = teamMember.getTeam().getProject();
        addProject(moment, teamMemberProject);
    }

    private List<Long> getAllTeamMemberIds(Moment moment) {
        return moment.getProjects().stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .sorted()
                .toList();
    }

    private void synchronizeMomentMembersAndProjects(Moment beforeUpdateMoment, Moment afterUpdateMoment) {
        List<Long> allTeamMembersIdsBefore = getAllTeamMemberIds(beforeUpdateMoment);
        List<Project> allProjectsBefore = beforeUpdateMoment.getProjects();
        List<Long> allTeamMembersIdsAfter = getAllTeamMemberIds(afterUpdateMoment);
        List<Project> allProjectsAfter = afterUpdateMoment.getProjects();

        //afterUpdateMoment.setProjects();


    }


    private void validateMoment(MomentRequestDto momentRequestDto) {
        if (StringUtils.isBlank(momentRequestDto.name())) {
            log.error("Moment cannot be with empty name!");
            throw new IllegalArgumentException("Moment cannot be with empty name!");
        }

        try {
            LocalDateTime date = parse(momentRequestDto.date(), formatter);
        } catch (Exception e) {
            log.error("Error converting date {} using format {}", momentRequestDto.date(), DATE_FORMAT);
            throw new IllegalArgumentException("Error converting date "
                    + momentRequestDto.date() + " using format " + DATE_FORMAT);
        }


        List<Long> projectIds = momentRequestDto.projectIds();
        if (projectIds == null || projectIds.isEmpty()) {
            log.error("Moment cannot be without projects!");
            throw new IllegalArgumentException("Moment cannot be without projects!");
        }

        List<Long> teamMembersIds = momentRequestDto.teamMembersIds();
    }
}
