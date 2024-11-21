package faang.school.projectservice.filter.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stageinvitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationDescriptionFilter implements Filter<StageInvitation, StageInvitationFilterDto> {

    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter != null &&
                filter.getDescriptionPattern() != null &&
                !filter.getDescriptionPattern().isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, StageInvitationFilterDto filter) {
        return invitations.filter(stageInvitation ->
                stageInvitation.getDescription().toLowerCase().
                        contains(filter.getDescriptionPattern().toLowerCase()));
    }
}
