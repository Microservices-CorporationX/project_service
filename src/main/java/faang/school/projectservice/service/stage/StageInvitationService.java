package faang.school.projectservice.service.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;

    public void sendInvitation(StageInvitation invitation) {
        stageInvitationRepository.save(invitation);
    }

}
