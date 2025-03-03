package ru.corporationx.projectservice.service.stageinvitation;

import ru.corporationx.projectservice.filters.stageinvitation.StageInvitationFilter;
import ru.corporationx.projectservice.model.dto.RejectionDto;
import ru.corporationx.projectservice.model.dto.invitation.StageInvitationDto;
import ru.corporationx.projectservice.model.dto.invitation.StageInvitationFilterDto;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.repository.jpa.StageInvitationJpaRepository;
import ru.corporationx.projectservice.mapper.stageinvitation.StageInvitationMapper;
import ru.corporationx.projectservice.model.entity.stage.Stage;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;
import ru.corporationx.projectservice.repository.StageRepository;
import ru.corporationx.projectservice.service.stageinvitation.StageInvitationServiceImpl;
import ru.corporationx.projectservice.service.teammember.TeamMemberService;
import ru.corporationx.projectservice.validator.stageinvitation.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceImplTest {
    private static final Long INVITATION_ID = 1L;
    private static final Long INVITED_ID = 2L;

    private final List<StageInvitationFilter> invitationFilters = new ArrayList<>();

    @Mock
    private StageInvitationJpaRepository invitationRepository;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private StageInvitationMapper invitationMapper;
    @Mock
    private StageInvitationValidator invitationValidator;

    private StageInvitationServiceImpl stageInvitationService;

    @BeforeEach
    public void setUp() {
        stageInvitationService = new StageInvitationServiceImpl(
                invitationRepository,
                teamMemberService,
                stageRepository,
                invitationMapper,
                invitationValidator,
                invitationFilters
        );
    }

    @Test
    public void testSendStageInvitationWithValidData() {
        StageInvitationDto invitationDto = StageInvitationDto.builder()
                .authorId(1L)
                .invitedId(INVITED_ID)
                .stageId(3L)
                .build();
        when(teamMemberService.findById(invitationDto.getAuthorId()))
                .thenReturn(new TeamMember());
        when(teamMemberService.findById(invitationDto.getInvitedId()))
                .thenReturn(new TeamMember());
        when(stageRepository.getById(invitationDto.getStageId()))
                .thenReturn(new Stage());
        ArgumentCaptor<StageInvitation> argumentCaptor = ArgumentCaptor.forClass(StageInvitation.class);

        stageInvitationService.sendStageInvitation(invitationDto);

        verify(invitationRepository).save(argumentCaptor.capture());
        assertEquals(StageInvitationStatus.PENDING, argumentCaptor.getValue().getStatus());
    }

    @Test
    public void testAcceptInvitationWithRejectedStatus() {
        Stage stage = Stage.builder()
                .executors(new ArrayList<>())
                .build();
        StageInvitation stageInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.REJECTED)
                .invited(new TeamMember())
                .stage(stage)
                .build();
        when(invitationValidator.getStageInvitation(INVITATION_ID))
                .thenReturn(stageInvitation);

        stageInvitationService.acceptStageInvitation(INVITATION_ID);

        verify(invitationRepository).save(stageInvitation);
        assertNull(stageInvitation.getDescription());
        assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
        assertEquals(1, stage.getExecutors().size());
    }

    @Test
    public void testRejectStageInvitationWithAcceptedStatus() {
        String rejectionReason = "test rejection reason";
        RejectionDto rejectionDto = RejectionDto.builder()
                .reason(rejectionReason)
                .build();
        TeamMember invited = new TeamMember();
        Stage stage = Stage.builder()
                .executors(new ArrayList<>(List.of(invited)))
                .build();
        StageInvitation stageInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.ACCEPTED)
                .invited(invited)
                .stage(stage)
                .build();
        when(invitationValidator.getStageInvitation(INVITATION_ID))
                .thenReturn(stageInvitation);

        stageInvitationService.rejectStageInvitation(INVITATION_ID, rejectionDto);

        verify(invitationRepository).save(stageInvitation);
        assertEquals(0, stage.getExecutors().size());
        assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
        assertEquals(rejectionReason, stageInvitation.getDescription());
    }

    @Test
    public void testGetInvitations() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();
        TeamMember invited = new TeamMember();
        StageInvitationFilter filterMock = mock(StageInvitationFilter.class);
        invitationFilters.add(filterMock);
        when(teamMemberService.findById(INVITED_ID))
                .thenReturn(invited);
        when(invitationRepository.findStageInvitationsByInvited(invited))
                .thenReturn(new ArrayList<>());
        when(filterMock.isApplicable(filterDto))
                .thenReturn(true);

        stageInvitationService.getInvitations(INVITED_ID, filterDto);

        verify(filterMock).isApplicable(filterDto);
        verify(filterMock).apply(any(), eq(filterDto));
    }
}