package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.mapper.invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.invitation.filter.InvitationFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private InvitationFilter authorFilter;

    @Mock
    private InvitationFilter statusFilter;

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @BeforeEach
    void setUp() {
        List<InvitationFilter> filters = Arrays.asList(authorFilter, statusFilter);
        ReflectionTestUtils.setField(stageInvitationService, "invitationFilters", filters);
    }

    @Test
    @DisplayName("Успешная отправка приглашения")
    public void testSendInvitation() {
        StageInvitationDTO stageInvitationDTO = new StageInvitationDTO();
        stageInvitationDTO.setStageId(1L);
        stageInvitationDTO.setInvitedId(2L);
        stageInvitationDTO.setAuthorId(3L);

        Stage stage = new Stage();
        TeamMember invited = new TeamMember();
        TeamMember author = new TeamMember();
        StageInvitation stageInvitation = new StageInvitation();

        when(stageRepository.getById(anyLong())).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenReturn(invited);
        when(teamMemberRepository.findById(3L)).thenReturn(author);
        when(stageInvitationMapper.toEntity(stageInvitationDTO)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(stageInvitation)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDTO);

        StageInvitationDTO result = stageInvitationService.sendInvitation(stageInvitationDTO);

        assertNotNull(result);
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
    }

    @Test
    @DisplayName("Должен выбросить EntityNotFoundException, если автор неверен")
    public void testSendInvitationWithInvalidAuthor() {
        StageInvitationDTO stageInvitationDTO = new StageInvitationDTO();
        stageInvitationDTO.setStageId(1L);
        stageInvitationDTO.setInvitedId(2L);
        stageInvitationDTO.setAuthorId(999L);

        when(stageRepository.getById(anyLong())).thenReturn(new Stage());
        when(teamMemberRepository.findById(anyLong()))
            .thenThrow(new EntityNotFoundException("Author not found"));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(stageInvitationDTO);
        });

        assertEquals("Author not found", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное принятие приглашения")
    public void testAcceptInvitation() {
        Stage stage = new Stage();
        stage.setExecutors(new ArrayList<>());

        StageInvitation invitation = new StageInvitation();
        invitation.setStage(stage);
        invitation.setInvited(new TeamMember());

        when(stageInvitationRepository.findById(anyLong())).thenReturn(invitation);
        when(stageInvitationRepository.save(any())).thenReturn(invitation);
        when(stageInvitationMapper.toDto(any())).thenReturn(new StageInvitationDTO());

        StageInvitationDTO result = stageInvitationService.acceptInvitation(1L);

        assertNotNull(result);
        verify(stageInvitationRepository, times(1)).save(invitation);
    }

    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при попытке принять несуществующее приглашение")
    public void testAcceptNonExistentInvitation() {
        when(stageInvitationRepository.findById(anyLong()))
            .thenThrow(new EntityNotFoundException("Invitation not found"));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.acceptInvitation(999L);
        });

        assertEquals("Invitation not found", exception.getMessage());
    }

    @Test
    @DisplayName("Успешный отказ от приглашения")
    public void testRejectInvitation() {
        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.PENDING);

        when(stageInvitationRepository.findById(anyLong())).thenReturn(invitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDTO expectedDto = new StageInvitationDTO();
        expectedDto.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationMapper.toDto(any())).thenReturn(expectedDto);

        StageInvitationDTO result = stageInvitationService.rejectInvitation(1L, "Reason");

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
        verify(stageInvitationRepository, times(1)).save(invitation);
    }

    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при попытке отклонить несуществующее приглашение")
    public void testRejectInvitation_NotFound() {
        when(stageInvitationRepository.findById(anyLong()))
            .thenThrow(new EntityNotFoundException("Invitation not found"));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.rejectInvitation(1L, "Reason");
        });

        assertEquals("Invitation not found", exception.getMessage());
        verify(stageInvitationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен вернуть отфильтрованные приглашения")
    void testGetFilteredInvitations() {
        StageInvitationDTO filterDTO = new StageInvitationDTO();
        List<StageInvitation> invitations = List.of(new StageInvitation());

        when(stageInvitationRepository.findAll()).thenReturn(invitations);
        when(authorFilter.isApplicable(any())).thenReturn(true);
        when(authorFilter.apply(any(), any())).thenReturn(invitations.stream());
        when(stageInvitationMapper.toDto(any())).thenReturn(filterDTO);

        List<StageInvitationDTO> result = stageInvitationService.getFilteredInvitations(filterDTO);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если фильтр не дал результатов")
    void testGetFilteredInvitationsWithNoResults() {
        StageInvitationDTO filterDTO = new StageInvitationDTO();
        when(stageInvitationRepository.findAll()).thenReturn(Collections.emptyList());

        List<StageInvitationDTO> result = stageInvitationService.getFilteredInvitations(filterDTO);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если фильтр равен null")
    void testGetFilteredInvitationsWithNullFilter() {
        when(stageInvitationRepository.findAll()).thenReturn(List.of(new StageInvitation()));

        List<StageInvitationDTO> result = stageInvitationService.getFilteredInvitations(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}

