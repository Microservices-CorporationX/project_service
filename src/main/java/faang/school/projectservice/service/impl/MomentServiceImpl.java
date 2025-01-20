package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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
import java.util.ArrayList;
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
        //Moment momentToSave = momentMapper.toMomentEntity(momentRequestDto);
        Moment momentSaved = momentRepository.save(momentMapper.toMomentEntity(momentRequestDto));
        MomentResponseDto createdMomentResponseDto = momentMapper.toMomentResponseDto(momentSaved);
        log.info("Created moment {}", createdMomentResponseDto);
        return createdMomentResponseDto;
    }

    @Override
    public MomentResponseDto updateMoment(MomentRequestDto momentRequestDto) {
        validateMomentId(momentRequestDto);
        validateMoment(momentRequestDto);

        MomentResponseDto initialMomentDto = this.getMoment(momentRequestDto.id());
        updateMomentMembersAndProjects(momentMapper.toMomentEntity(initialMomentDto),
                momentMapper.toMomentEntity(momentRequestDto));

        Moment momentSaved = momentRepository.save(momentMapper.toMomentEntity(momentRequestDto));
        return createMoment(momentRequestDto);
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

    private void validateMomentId(MomentRequestDto momentRequestDto)
    {
        if (StringUtils.isBlank(String.valueOf(momentRequestDto.id()))) {
            log.error("Unable update moment, because it's Id is null {}", momentRequestDto);
            throw new IllegalArgumentException("Unable update moment, because it's Id is null");
        }
    }

    /*private void addProject(Moment moment, Project project) {
        List<Project> projects = moment.getProjects();
        projects.add(project);
        moment.setProjects(projects);
        List<Long> teamMemberIds = getAllProjectsTeamMemberIds(moment);
        moment.setUserIds(teamMemberIds);

    }

    private void addTeamMember(Moment moment, TeamMember teamMember) {
        List<Long> teamMemberIds = moment.getUserIds();
        teamMemberIds.add(teamMember.getId());
        moment.setUserIds(teamMemberIds);
        Project teamMemberProject = teamMember.getTeam().getProject();
        addProject(moment, teamMemberProject);
    }*/

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

    private Moment updateMomentMembersAndProjects(Moment initialMoment, Moment updatedMoment) {
        List<Long> initialAllProjectTeamMembersIds = getAllProjectsTeamMemberIds(initialMoment);
        List<Long> addedProjectTeamMembersIds = getAllProjectsTeamMemberIds(updatedMoment);
        List<Long> initialTeamMembersIds = initialMoment.getUserIds();
        List<Long> addedTeamMembersIds = updatedMoment.getUserIds();
        List<Project> initialAllProjects = initialMoment.getProjects();
        List<Project> addedProjects = updatedMoment.getProjects();

        List<Long> resultTeamMemberIds = new ArrayList<>();
        List<Project> resultProjectIds = new ArrayList<>();

        resultTeamMemberIds.addAll(initialTeamMembersIds);
        resultTeamMemberIds.addAll(initialAllProjectTeamMembersIds);
        resultTeamMemberIds.addAll(addedProjectTeamMembersIds);
        resultTeamMemberIds.addAll(addedTeamMembersIds);

        resultProjectIds.addAll(initialAllProjects);
        resultProjectIds.addAll(addedProjects);

        updatedMoment.setUserIds(resultTeamMemberIds.stream().distinct().sorted().toList());
        updatedMoment.setProjects(resultProjectIds.stream().distinct().sorted().toList());

        return updatedMoment;
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


        List<Long> projectIds = momentRequestDto.projectToAddIds();
        if (null == projectIds || projectIds.isEmpty()) {
            log.error("Moment cannot be without projects!");
            throw new IllegalArgumentException("Moment cannot be without projects!");
        }

        List<Long> teamMembersIds = momentRequestDto.teamMemberToAddIds();
    }
}
