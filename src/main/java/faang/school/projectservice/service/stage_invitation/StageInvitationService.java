package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@RequiredArgsConstructor
@Validated
public class StageInvitationService {
    private final StageInvitationRepository invitationRepository;
    private final StageInvitationMapper invitationMapper;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final List<StageInvitationFilter> stageInvitationFilters;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitationEntity = invitationMapper.toEntity(stageInvitationDto);

        stageInvitationEntity.setStage(stageRepository.getById(stageInvitationDto.getStageId()));
        stageInvitationEntity.setAuthor(teamMemberRepository.findById(stageInvitationDto.getAuthorId()));
        stageInvitationEntity.setInvited(teamMemberRepository.findById(stageInvitationDto.getInvitedId()));
        stageInvitationEntity.setStatus(StageInvitationStatus.PENDING);

        stageInvitationEntity = invitationRepository.save(stageInvitationEntity);
        return invitationMapper.toDto(stageInvitationEntity);
    }

    public StageInvitationDto acceptInvitation(Long invitationId, Long invitedId) {
        StageInvitation stageInvitation = invitationRepository.findById(invitationId);

        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not in a PENDING state");
        }
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        stageInvitation.setInvited(teamMemberRepository.findById(invitedId));

        StageInvitation saveInvitation = invitationRepository.save(stageInvitation);
        return invitationMapper.toDto(saveInvitation);
    }

    public StageInvitationDto rejectInvitation(Long invitationId, Long invitedId, String rejectDescription) {
        StageInvitation stageInvitation = invitationRepository.findById(invitationId);

        validation(stageInvitation, invitedId, rejectDescription);

        stageInvitation.setDescription(rejectDescription);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        stageInvitation.setInvited(teamMemberRepository.findById(invitedId));

        StageInvitation saveInvitation = invitationRepository.save(stageInvitation);
        return invitationMapper.toDto(saveInvitation);
    }

    public List<StageInvitationDto> checkAllInvitation(Long invitedId, StageInvitationFilterDto filters) {
        List<StageInvitation> invitationList = invitationRepository.findAllByInvitedId(invitedId);

        return stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(invitationList.stream(), filters))
                .map(invitationMapper::toDto)
                .toList();
    }

    private void validation(StageInvitation stageInvitation, Long invitedId, String rejectDescription) {
        if (rejectDescription == null && rejectDescription.isEmpty()) {
            throw new IllegalArgumentException("description не может быть равен null или быть пустым");
        }

        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not in a PENDING state");
        }

        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new IllegalArgumentException("");
        }
    }
}
