package faang.school.projectservice.filters.impl;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationStageFilterImplTest {
    private final static Long STAGE_ID = 1L;

    private final InvitationStageFilterImpl stageFilter = new InvitationStageFilterImpl();

    @Test
    public void testIsApplicable() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .stageId(STAGE_ID)
                .build();

        boolean isApplicable = stageFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        boolean isApplicable = stageFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyStageFilter() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .stageId(STAGE_ID)
                .build();
        Stage firstStage = Stage.builder()
                .stageId(STAGE_ID)
                .build();
        Stage secondStage = Stage.builder()
                .stageId(STAGE_ID + 1)
                .build();
        StageInvitation firstInvitation = StageInvitation.builder()
                .stage(firstStage)
                .build();
        StageInvitation secondInvitation = StageInvitation.builder()
                .stage(secondStage)
                .build();
        Stream<StageInvitation> invitationStream = Stream.of(firstInvitation, secondInvitation);

        List<StageInvitation> resultList = stageFilter.apply(invitationStream, filterDto).toList();

        assertEquals(1, resultList.size());
        assertEquals(STAGE_ID, resultList.get(0).getStage().getStageId());
    }
}