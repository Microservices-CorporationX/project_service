package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.task.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageJpaRepository stageJpaRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilterList;
    private final TaskService taskService;
    private final StageInvitationService stageInvitationService;

    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toStage(stageDto);

        return stageMapper.toStageDto(stageJpaRepository.save(stage));
    }

    public List<StageDto> getAllStagesByFilters(StageFilterDto stageFilterDto) {

        Stream<Stage> stageStream = stageJpaRepository.findAll().stream();

        return stageFilterList.stream()
                .filter(filter -> filter.isApplicable(stageFilterDto))
                .reduce(stageStream,
                        (stream, filter) -> filter.apply(stream, stageFilterDto),
                        (s1, s2) -> s1)
                .map(stageMapper::toStageDto)
                .toList();
    }

    public void deleteStageById(Long id) {

        Stage stage = stageJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        taskService.saveAll(stage.getTasks().stream()
                .peek(task -> task.setStatus(TaskStatus.CANCELLED))
                .toList());

        stageJpaRepository.deleteById(id);
    }

    public StageDto updateStage(Long stageId) {

        Stage stage = stageJpaRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        stage.getStageRoles()
                .forEach(role -> {
                    long absenceByRole = getAbsenceTeamMembersByRole(stage, role);
                    if (absenceByRole > 0) {
                        stage.getProject().getTeams().stream()
                                .flatMap(team -> team.getTeamMembers().stream())
                                .filter(teamMember -> teamMember.getRoles().contains(role.getTeamRole()))
                                .limit(absenceByRole)
                                .forEach(teamMember -> {
                                    stageInvitationService.sendInvitation(StageInvitation.builder().stage(stage).build());
                                });
                    }
                });
        return stageMapper.toStageDto(stageJpaRepository.save(stage));
    }

    private Long getAbsenceTeamMembersByRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> teamMembers = stage.getExecutors();
        Long countTeamMembers = teamMembers.stream()
                .filter(teamMember -> teamMember.getRoles()
                        .contains(stageRoles.getTeamRole())
                )
                .count();
        return stageRoles.getCount() - countTeamMembers;
    }
}
