package faang.school.projectservice.filters;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

@AllArgsConstructor
public class DateFilter implements InvitationFilter {
    private final LocalDate date;

    @Override
    public boolean apply(StageInvitation invitation) {
        return invitation.getCreatedDate().toLocalDate().equals(date);
    }
}
