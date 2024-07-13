package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.mapper.stage.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.stage.StageInvitationDtoValidator;
import faang.school.projectservice.validator.stage.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationDtoValidator stageInvitationDtoValidator;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final List<StageInvitationFilter> stageInvitationFilters;
    private final StageInvitationValidator stageInvitationValidator;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        stageInvitationDtoValidator.validateAll(stageInvitationDto);

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto, teamMemberRepository);
        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptStageInvitation(Long id) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(id);
        TeamMember invited = stageInvitation.getInvited();
        stageInvitationValidator.statusPendingCheck(stageInvitation);

        invited.getStages().add(stageInvitation.getStage());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto rejectStageInvitation(Long id, String rejectionReason) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(id);
        TeamMember invited = stageInvitation.getInvited();
        stageInvitationValidator.statusPendingCheck(stageInvitation);

        invited.getStages().remove(stageInvitation.getStage());
        stageInvitation.setRejectionReason(rejectionReason);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> getMemberStageInvitations(Long id, StageInvitationFilterDto filter) {
        Stream<StageInvitation> stageInvitations = stageInvitationRepository.findAll().stream();

        stageInvitations
                .filter(stageInvitation -> stageInvitation.getInvited().getId().equals(id));

        return stageInvitationFilters.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(filter))
                .flatMap(stageInvitationFilter -> stageInvitationFilter.apply(stageInvitations, filter))
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}
