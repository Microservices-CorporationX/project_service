package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.stage_invitation_filter.StageInvitationDescriptionFilter;
import faang.school.projectservice.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.filter.stage_invitation_filter.StageInvitationStageNameFilter;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationMapper;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validator.StageInvitationValidator;
import faang.school.projectservice.validator.StageValidator;
import faang.school.projectservice.validator.TeamMemberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {

    @Spy
    StageInvitationMapper stageInvitationMapper;

    @Mock
    StageInvitationJpaRepository repository;

    @Mock
    StageInvitationValidator stageInvValidator;

    @Mock
    TeamMemberValidator teamMemberValidator;

    @Mock
    StageValidator stageValidator;

    private StageInvitationService service;
    private StageInvitationFilter mockDescriptionFilter;
    private StageInvitationFilter mockStageNameFilter;

    @BeforeEach
    void setUp() {
        stageInvitationMapper = new StageInvitationMapperImpl();
        mockDescriptionFilter = mock(StageInvitationDescriptionFilter.class);
        mockStageNameFilter = mock(StageInvitationStageNameFilter.class);

        List<StageInvitationFilter> filters = new ArrayList<>(
                List.of(mockDescriptionFilter, mockStageNameFilter));

        service = new StageInvitationService(filters, stageInvitationMapper, repository,
                stageInvValidator, teamMemberValidator, stageValidator);
    }

    @Test
    public void sendStageInvitation() {
        StageInvitationDto dto = StageInvitationDto.builder()
                .id(505L)
                .description("description")
                .stageId(15L)
                .authorId(10L)
                .invitedId(1L)
                .build();

        TeamMember author = new TeamMember();
        TeamMember invited = new TeamMember();
        Stage stage = new Stage();

        StageInvitation invitation = stageInvitationMapper.toEntity(dto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStage(stage);

        doNothing().when(stageInvValidator).
                validateStageInvitationExists(dto.getId());
        when(teamMemberValidator.validateTeamMemberExists(dto.getAuthorId())).
                thenReturn(author);
        when(teamMemberValidator.validateTeamMemberExists(dto.getInvitedId())).
                thenReturn(invited);
        when(stageValidator.validateStageExists(dto.getStageId())).
                thenReturn(stage);

        service.sendStageInvitation(dto.getAuthorId(), dto);

        ArgumentCaptor<StageInvitation> captor = ArgumentCaptor.forClass(StageInvitation.class);

        verify(stageInvValidator).validateStageInvitationExists(dto.getId());
        verify(teamMemberValidator).validateTeamMemberExists(dto.getAuthorId());
        verify(teamMemberValidator).validateTeamMemberExists(dto.getInvitedId());
        verify(stageValidator).validateStageExists(dto.getStageId());
        verify(repository).save(captor.capture());

        StageInvitation capturedInvitation = captor.getValue();

        assertEquals(invitation, capturedInvitation);
    }

    @Test
    public void acceptStageInvitationTest() {
        long stageInvId = 10L;
        long invitedId = 1L;

        TeamMember teamMember = new TeamMember();
        Stage stage = new Stage();
        stage.setExecutors(new ArrayList<>());
        StageInvitation invitation = new StageInvitation();
        invitation.setStage(stage);

        when(stageInvValidator.validateStageInvitationNotExists(stageInvId)).
                thenReturn(invitation);
        doNothing().when(stageInvValidator).
                validateIsInvitationSentToThisTeamMember(invitedId, stageInvId);
        when(teamMemberValidator.validateIsTeamMemberParticipantOfProject(invitedId, invitation)).
                thenReturn(teamMember);

        service.acceptStageInvitation(invitedId, stageInvId);

        ArgumentCaptor<StageInvitation> captor = ArgumentCaptor.forClass(StageInvitation.class);

        verify(stageInvValidator).validateStageInvitationNotExists(stageInvId);
        verify(stageInvValidator).validateIsInvitationSentToThisTeamMember(invitedId, stageInvId);
        verify(teamMemberValidator)
                .validateIsTeamMemberParticipantOfProject(invitedId, invitation);
        verify(repository).save(captor.capture());

        StageInvitation capturedInvitation = captor.getValue();

        assertEquals(invitation, capturedInvitation);
        assertEquals(StageInvitationStatus.ACCEPTED, capturedInvitation.getStatus());
        assertEquals(new ArrayList<>(List.of(teamMember)), capturedInvitation.getStage().getExecutors());
    }

    @Test
    public void rejectStageInvitationReasonIsNullTest() {
        long stageInvId = 10L;
        long invitedId = 1L;
        String reason = null;

        assertThrows(DataValidationException.class,
                () -> service.rejectStageInvitation(invitedId, stageInvId, reason));
    }

    @Test
    public void rejectStageInvitationReasonIsBlancTest() {
        long stageInvId = 10L;
        long invitedId = 1L;
        String reason = "";

        assertThrows(DataValidationException.class,
                () -> service.rejectStageInvitation(invitedId, stageInvId, reason));
    }

    @Test
    public void rejectStageInvitationTest() {
        long stageInvId = 10L;
        long invitedId = 1L;
        String reason = "reason";
        StageInvitation invitation = new StageInvitation();
        invitation.setId(stageInvId);

        when(stageInvValidator.validateStageInvitationNotExists(stageInvId)).
                thenReturn(invitation);
        doNothing().when(stageInvValidator).
                validateIsInvitationSentToThisTeamMember(invitedId, stageInvId);

        ArgumentCaptor<StageInvitation> captor = ArgumentCaptor.forClass(StageInvitation.class);

        service.rejectStageInvitation(invitedId, stageInvId, reason);

        verify(stageInvValidator).validateStageInvitationNotExists(stageInvId);
        verify(stageInvValidator).validateIsInvitationSentToThisTeamMember(invitedId, stageInvId);
        verify(repository).save(captor.capture());

        StageInvitation capturedInvitation = captor.getValue();

        assertEquals(invitation.getId(), capturedInvitation.getId());
        assertEquals(reason, capturedInvitation.getDescription());
        assertEquals(StageInvitationStatus.REJECTED, capturedInvitation.getStatus());
    }

    @Test
    void getStageInvitationsWithFiltersAppliedTest() {
        long invitedId = 1L;
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        TeamMember invitedMember = new TeamMember();
        invitedMember.setId(invitedId);

        StageInvitation invitation1 = new StageInvitation();
        invitation1.setInvited(invitedMember);
        StageInvitation invitation2 = new StageInvitation();
        invitation2.setInvited(invitedMember);

        StageInvitationDto dto1 = new StageInvitationDto();
        dto1.setInvitedId(invitedId);
        StageInvitationDto dto2 = new StageInvitationDto();
        dto2.setInvitedId(invitedId);

        when(repository.findAll()).thenReturn(List.of(invitation1, invitation2));
        when(mockDescriptionFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockStageNameFilter.isApplicable(filterDto)).thenReturn(false);
        when(mockDescriptionFilter.apply(any(), eq(filterDto)))
                .thenAnswer(invocations -> invocations.getArgument(0));

        List<StageInvitationDto> result = service.getStageInvitations(invitedId, filterDto);

        verify(repository).findAll();
        verify(mockDescriptionFilter).isApplicable(filterDto);
        verify(mockStageNameFilter).isApplicable(filterDto);
        verify(mockDescriptionFilter).apply(any(), eq(filterDto));

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }
}