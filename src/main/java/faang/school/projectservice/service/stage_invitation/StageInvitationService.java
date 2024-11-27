package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
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

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitationEntity = invitationMapper.toEntity(stageInvitationDto);

//        Stage stage = stageRepository.getById(stageInvitationDto.getStageId());
//        TeamMember author = teamMemberRepository.findById(stageInvitationDto.getAuthorId());
//        TeamMember invitation = teamMemberRepository.findById(stageInvitationDto.getInvitedId());

        stageInvitationEntity.setStage(stageRepository.getById(stageInvitationDto.getStageId()));
        stageInvitationEntity.setAuthor(teamMemberRepository.findById(stageInvitationDto.getAuthorId()));
        stageInvitationEntity.setInvited(teamMemberRepository.findById(stageInvitationDto.getInvitedId()));
        stageInvitationEntity.setStatus(StageInvitationStatus.PENDING);

        //StageInvitation stageInvitation = new StageInvitation();
//        stageInvitation.setStage(stage);
//        stageInvitation.setAuthor(author);
//        stageInvitation.setInvited(invitation);

        stageInvitationEntity = invitationRepository.save(stageInvitationEntity);
        return invitationMapper.toDto(stageInvitationEntity);
    }

    public StageInvitationDto acceptInvitation(Long invintationId, Long invitedId) {
        StageInvitation stageInvitation = invitationRepository.findById(invintationId);

        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not in a PENDING state");
        }
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        stageInvitation.setInvited(teamMemberRepository.findById(invitedId));

        StageInvitation saveInvitation = invitationRepository.save(stageInvitation);
        return invitationMapper.toDto(saveInvitation);
    }

    public StageInvitationDto rejectInvitation(Long invintationId, Long invitedId, String rejectDescription) {
        if (rejectDescription == null && rejectDescription.isEmpty()) {
            throw new IllegalArgumentException("description не может быть равен null или быть пустым");
        }

        StageInvitation stageInvitation = invitationRepository.findById(invintationId);

        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not in a PENDING state");
        }

        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new IllegalArgumentException("");
        }

        stageInvitation.setDescription(rejectDescription);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        stageInvitation.setInvited(teamMemberRepository.findById(invitedId));

        StageInvitation saveInvitation = invitationRepository.save(stageInvitation);
        return invitationMapper.toDto(saveInvitation);
    }

    public void checkAllInvitation(Long invintationId) {
        StageInvitation stageInvitation = invitationRepository.findById(invintationId);
    }

}
