package ru.corporationx.projectservice.filters.stageinvitation;

import ru.corporationx.projectservice.model.dto.invitation.StageInvitationFilterDto;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.projectservice.filters.stageinvitation.InvitationDescriptionFilterImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationDescriptionFilterImplTest {
    private static final String TEST_DESCRIPTION = "test";

    private final InvitationDescriptionFilterImpl descriptionFilter = new InvitationDescriptionFilterImpl();

    @Test
    public void testIsApplicable() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .description(TEST_DESCRIPTION)
                .build();

        boolean isApplicable = descriptionFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        boolean isApplicable = descriptionFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyDescriptionFilter() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .description(TEST_DESCRIPTION)
                .build();
        StageInvitation firstInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.REJECTED)
                .description("test123")
                .build();
        StageInvitation secondInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.REJECTED)
                .description("123")
                .build();
        Stream<StageInvitation> invitationStream = Stream.of(firstInvitation, secondInvitation);

        List<StageInvitation> resultList = descriptionFilter.apply(invitationStream, filterDto).toList();

        assertEquals(1, resultList.size());
        assertTrue(resultList.get(0).getDescription().contains(TEST_DESCRIPTION));
    }
}