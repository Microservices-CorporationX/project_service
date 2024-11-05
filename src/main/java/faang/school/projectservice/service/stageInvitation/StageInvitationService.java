package faang.school.projectservice.service.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final TeamMemberRepository teamMemberRepository;        //Create Service
    private final StageRepository stageRepository;              //Create service

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationJpaRepository repository;

    public void sendStageInvitation(long invitorId, StageInvitationDto dto) {
        stageInvitationExistenceCheck(dto.getId());
        //is invited/invitor ,team member //
        TeamMember author = teamMemberRepository.findById(invitorId);
        TeamMember invited = teamMemberRepository.findById(dto.getInvitedId());
        Stage stageToInvite = stageRepository.getById(dto.getStageId()); //throws entity not found ^

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(dto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setStage(stageToInvite);
        stageInvitation.setAuthor(author);
        stageInvitation.setInvited(invited);

        repository.save(stageInvitation);
    }

    public void acceptStageInvitation(long invitorId, long stageInvitationId) {
        // is this invitation sent to this user? //
    }

    private void stageInvitationExistenceCheck(long id) {
        repository.findById(id).orElseThrow(
                //log
                () -> new EntityNotFoundException("Stage Invitation with id " + id + " already exists"));
    }
}
