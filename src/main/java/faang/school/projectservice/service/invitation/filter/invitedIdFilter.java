package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class invitedIdFilter implements InvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationDTO filter) {
        return filter.getInvitedId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationDTO filterDTO) {
        return stageInvitationStream.filter(invitation ->
            Objects.equals(invitation.getInvited(), filterDTO.getInvitedId())
        );
    }
}
