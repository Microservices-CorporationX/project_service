package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.StageInvitationFilters;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class StageInvitationStageIdFilter implements Filter<StageInvitation, StageInvitationFilters> {
    @Override
    public boolean isApplicable(StageInvitationFilters filters) {
        return filters.getStageId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stages, StageInvitationFilters filters) {
        return stages.filter(stage -> Objects.equals(stage.getStage().getStageId(), filters.getStageId()));
    }
}
