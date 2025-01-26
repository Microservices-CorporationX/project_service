package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageinvitation.ChangeStatusDto;
import faang.school.projectservice.dto.stageinvitation.RejectInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.filter.stageinvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.stageinvitation.*;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.stageinvitation.StageInvitationValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {
    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;

    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Spy
    private ChangeStatusMapperImpl changeStatusMapper;

    @Spy
    private RejectInvitationMapperImpl rejectInvitationMapper;

    @Mock
    private List<StageInvitationFilter> stageInvitationFilters;

    @Spy
    private InvitationUpdateMapperImpl invitationUpdateMapper;

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Test
    public void shouldThrowFindId() {
        Mockito.when(stageInvitationRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stageInvitationService.findById(Mockito.anyLong()));

        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    public void shouldFindId() {
        Optional<StageInvitation> optional = Optional.of(StageInvitation.builder().id(1L).build());
        StageInvitation stageInvitation = optional.get();

        Mockito.when(stageInvitationRepository.findById(Mockito.anyLong()))
                .thenReturn(optional);

        Assertions.assertEquals(stageInvitationService.findById(1L), stageInvitation);

        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void shouldCreateStageInvitation() {
        Long id = 5L;

        StageInvitationDto dto = StageInvitationDto.builder()
                .invitedId(id)
                .stageId(id)
                .authorId(id)
                .build();

        Stage stage = Stage.builder().stageId(id).build();
        TeamMember teamMember = TeamMember.builder().id(id).build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .author(teamMember)
                .stage(stage)
                .invited(teamMember)
                .status(StageInvitationStatus.PENDING)
                .build();

        Mockito.when(stageInvitationValidator.validateStageInvitation(dto))
                .thenReturn(StageInvitation.builder().id(id).build());
        Mockito.when(teamMemberService.findById(id))
                .thenReturn(teamMember)
                .thenReturn(teamMember);
        Mockito.when(stageService.findById(id)).thenReturn(stage);
        Mockito.when(stageInvitationRepository.save(stageInvitation))
                .thenReturn(stageInvitation);

        Assertions.assertEquals(stageInvitationService.createStageInvitation(dto), dto);

        Mockito.verify(stageInvitationValidator, Mockito.times(1)).validateStageInvitation(dto);
        Mockito.verify(stageService, Mockito.times(1)).findById(id);
        Mockito.verify(teamMemberService, Mockito.times(2)).findById(id);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(stageInvitation);
    }

    @Test
    public void shouldUpdateStageInvitation() {
        Long id = 5L;

        StageInvitationUpdateDto dto = StageInvitationUpdateDto.builder()
                .id(id)
                .stageId(id)
                .description("content")
                .build();

        Stage stage = Stage.builder()
                .stageId(id)
                .build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .description("description")
                .stage(Stage.builder()
                        .stageId(6L)
                        .build())
                .build();

        StageInvitation result = StageInvitation.builder().id(id)
                .description(stageInvitation.getDescription())
                .stage(stage)
                .build();
        invitationUpdateMapper.update(result, dto);

        Mockito.doNothing().when(stageInvitationValidator)
                .validateUpdateInvitation(dto);
        Mockito.when(stageService.findById(id)).thenReturn(stage);
        Mockito.when(stageInvitationRepository.findById(id))
                .thenReturn(Optional.of(result));
        Mockito.when(stageInvitationRepository.save(result)).thenReturn(result);

        Assertions.assertEquals(dto, stageInvitationService.updateStageInvitation(
                invitationUpdateMapper.toDto(result)));

        Mockito.verify(stageInvitationValidator, Mockito.times(1)).validateUpdateInvitation(dto);
        Mockito.verify(stageService, Mockito.times(1)).findById(id);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).save(result);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void shouldThrowStatusNotPending() {
        Long id = 5L;

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        Mockito.when(stageInvitationRepository.findById(id))
                .thenReturn(Optional.of(stageInvitation));

        ChangeStatusDto statusDto = changeStatusMapper.toDto(stageInvitation);

        Assertions.assertThrows(BusinessException.class,
                () -> stageInvitationService.acceptStageInvitation(statusDto));

        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(id);
    }


    @Test
    public void shouldThrowUserHasStage() {
        Long id = 5L;

        Stage stage = Stage.builder().stageId(id).build();
        TeamMember invited = TeamMember.builder()
                .id(id)
                .stages(List.of(stage))
                .build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .build();

        ChangeStatusDto dto = changeStatusMapper.toDto(stageInvitation);

        Mockito.when(stageInvitationRepository.findById(id))
                .thenReturn(Optional.of(stageInvitation));
        Mockito.when(teamMemberService.findById(id)).thenReturn(invited);


        Assertions.assertThrows(BusinessException.class,
                () -> stageInvitationService.acceptStageInvitation(dto)
                , "Пользователь уже является исполнителем этого этапа");

        Mockito.verify(teamMemberService, Mockito.times(1)).findById(id);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void shouldAcceptStageInvitation() {
        Long id = 5L;

        Stage stage = Stage.builder().stageId(id).build();
        TeamMember invited = TeamMember.builder()
                .id(id)
                .stages(new ArrayList<>())
                .build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .build();

        StageInvitation result = StageInvitation.builder()
                .id(id)
                .invited(invited)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        ChangeStatusDto dto = changeStatusMapper.toDto(result);

        Mockito.when(stageInvitationRepository.findById(id))
                .thenReturn(Optional.of(stageInvitation));
        Mockito.when(teamMemberService.findById(id)).thenReturn(invited);

        Assertions.assertEquals(changeStatusMapper.toDto(result), stageInvitationService.acceptStageInvitation(dto));

        Mockito.verify(teamMemberService, Mockito.times(1)).findById(id);
        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void shouldRejectStageInvitation() {
        Long id = 5L;

        Stage stage = Stage.builder().stageId(id).build();
        TeamMember invited = TeamMember.builder()
                .id(id)
                .stages(new ArrayList<>())
                .build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .build();

        StageInvitation result = StageInvitation.builder()
                .id(id)
                .invited(invited)
                .status(StageInvitationStatus.REJECTED)
                .build();

        RejectInvitationDto dto = rejectInvitationMapper.toDto(stageInvitation);

        Mockito.when(stageInvitationRepository.findById(id))
                .thenReturn(Optional.of(stageInvitation));

        Assertions.assertEquals(rejectInvitationMapper.toDto(result),
                stageInvitationService.rejectStageInvitation(dto));

        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void shouldGetStageInvitationForTeamMember() {
        Long id = 5L;

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(id)
                .invited(TeamMember.builder().id(id).build())
                .build();

        List<StageInvitation> stageInvitations = List.of(stageInvitation);

        Mockito.when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));

        Assertions.assertEquals(stageInvitations.stream().map(stageInvitationMapper::toDto).toList(),
                stageInvitationService.getStageInvitationForTeamMember(id));

        Mockito.verify(stageInvitationRepository, Mockito.times(1)).findAll();
    }
}