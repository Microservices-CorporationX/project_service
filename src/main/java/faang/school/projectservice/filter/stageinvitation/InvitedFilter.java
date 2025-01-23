package faang.school.projectservice.filter.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InvitedFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getInvitedId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> events, StageInvitationFilterDto filter) {
        return events.filter(event -> event
                .getInvited()
                .getId()
                .equals(filter.getInvitedId()));
    }
}
