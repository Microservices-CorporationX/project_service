package faang.school.projectservice.filter.stageinvitationfilter;

import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stageinvitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationProjectNameFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter != null &&
                filter.getProjectNamePattern() != null &&
                !filter.getProjectNamePattern().isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, StageInvitationFilterDto filter) {
        return invitations.filter(
                stageInvitation -> stageInvitation.getStage().getProject().getName().toLowerCase().
                        contains(filter.getProjectNamePattern().toLowerCase()));
    }
}
