package ru.corporationx.projectservice.filters.stageinvitation;

import ru.corporationx.projectservice.model.dto.invitation.StageInvitationFilterDto;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InvitationStageFilterImpl implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getStageId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitationStream,
                                         StageInvitationFilterDto filterDto) {
        return invitationStream.filter(stageInvitation ->
                stageInvitation.getStage().getStageId().equals(filterDto.getStageId()));
    }
}
