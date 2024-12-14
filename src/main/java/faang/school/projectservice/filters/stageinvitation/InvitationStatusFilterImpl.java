package faang.school.projectservice.filters.stageinvitation;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InvitationStatusFilterImpl implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitationStream,
                                         StageInvitationFilterDto filterDto) {
        return invitationStream.filter(stageInvitation ->
                stageInvitation.getStatus().equals(filterDto.getStatus()));
    }
}
