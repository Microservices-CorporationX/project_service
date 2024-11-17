package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.Objects;
import java.util.stream.Stream;

public class StatusFilter implements InvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationDTO filter) {
        return filter.getStatusId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationDTO filter) {
        return stageInvitationStream.filter(stageInvitation ->
            Objects.equals(stageInvitation.getStatus().getStatus(), filter.getStatus()));
    }
}
