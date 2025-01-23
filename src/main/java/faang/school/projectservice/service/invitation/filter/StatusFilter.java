package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.dto.invitation.InvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.Objects;
import java.util.stream.Stream;

public class StatusFilter implements InvitationFilter   {

    @Override
    public boolean isApplicable(InvitationDto filter) {
        return filter.status() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, InvitationDto filter) {
        return stageInvitationStream.filter(invitation ->
                Objects.equals(invitation.getStatus(), filter.status()));
    }
}
