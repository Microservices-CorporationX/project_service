package faang.school.projectservice.filter.stage_invitation_filter;

import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStageNameFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getStageNamePattern() != null && !filter.getStageNamePattern().isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, StageInvitationFilterDto filter) {
        return invitations.filter(stageInvitation ->
                stageInvitation.getStage().getStageName().contains(filter.getStageNamePattern()));
    }
}