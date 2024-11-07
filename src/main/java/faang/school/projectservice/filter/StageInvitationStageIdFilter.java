package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFiltersDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class StageInvitationStageIdFilter implements Filter<StageInvitation, StageInvitationFiltersDto> {
    @Override
    public boolean isApplicable(StageInvitationFiltersDto filters) {
        return filters.getStageId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stages, StageInvitationFiltersDto filters) {
        return stages.filter(stage -> Objects.equals(stage.getStage().getStageId(), filters.getStageId()));
    }
}
