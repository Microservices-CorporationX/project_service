package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StageInvitationServiceTest {
    @InjectMocks
    private StageInvitationService invitationService;
    @Mock
    private StageInvitationRepository invitationRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private List<StageInvitationFilter> stageInvitationFilters;
    @Spy
    private StageInvitationMapper invitationMapper = Mappers.getMapper(StageInvitationMapper.class);
    @Captor
    private ArgumentCaptor<StageInvitation> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

        @Test
    public void testCreateStageInvitation() {
        StageInvitationDto invitationDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.PENDING)
                .stageId(1L)
                .authorId(1L)
                .invitedId(1L)
                .build();

        Stage stage = new Stage();
        stage.setStageId(1L);

        TeamMember author = new TeamMember();
        author.setId(1L);
        TeamMember invited = new TeamMember();
        invited.setId(1L);

        when(stageRepository.getById(invitationDto.getStageId())).thenReturn(stage);
        when(teamMemberRepository.findById(invitationDto.getAuthorId())).thenReturn(author);
        when(teamMemberRepository.findById(invitationDto.getInvitedId())).thenReturn(invited);
        when(invitationRepository.save(any(StageInvitation.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        StageInvitationDto result = invitationService.create(invitationDto);

        verify(invitationRepository, times(1)).save(captor.capture());

        StageInvitation stageInvitation = captor.getValue();
        assertEquals(invitationDto.getStageId(), stageInvitation.getStage().getStageId());
        assertEquals(invitationDto.getAuthorId(), stageInvitation.getAuthor().getId());
        assertEquals(invitationDto.getInvitedId(), stageInvitation.getInvited().getId());

        assertEquals(invitationDto.getId(), result.getId());
    }
}
