package faang.school.projectservice.validator.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stageinvitation.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class StageInvitationValidatorTest {
    @InjectMocks
    private StageInvitationValidator validator;

    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;

    @Test
    public void shouldThrowDescriptionBlank() {
        StageInvitation stageInvitation = StageInvitation.builder()
                .description(" ")
                .build();

        StageInvitationDto dto = stageInvitationMapper.toDto(stageInvitation);

        Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateStageInvitation(dto),
                "Описание не должно быть пустым");
    }

    @Test
    public void shouldThrowStageNull() {
        StageInvitation stageInvitation = StageInvitation.builder()
                .description("asda")
                .build();

        StageInvitationDto dto = stageInvitationMapper.toDto(stageInvitation);

        Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateStageInvitation(dto),
                "Id этапа не должен быть null");
    }

    @Test
    public void shouldThrowAuthorNull() {
        Long id = 1L;

        StageInvitation stageInvitation = StageInvitation.builder()
                .description("asda")
                .stage(Stage.builder().stageId(id).build())
                .build();

        StageInvitationDto dto = stageInvitationMapper.toDto(stageInvitation);

        Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateStageInvitation(dto),
                "Id автора не должен быть null");
    }

    @Test
    public void shouldThrowInvitedNull() {
        Long id = 1L;

        StageInvitation stageInvitation = StageInvitation.builder()
                .description("asda")
                .author(TeamMember.builder().id(id).build())
                .stage(Stage.builder().stageId(id).build())
                .build();

        StageInvitationDto dto = stageInvitationMapper.toDto(stageInvitation);

        Assertions.assertThrows(DataValidationException.class,
                () -> validator.validateStageInvitation(dto),
                "Id приглашаемого не должен быть null");
    }

    @Test
    public void shouldSuccessValidate() {
        Long id = 1L;

        TeamMember teamMember = TeamMember.builder().id(id).build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .description("asda")
                .author(teamMember)
                .invited(teamMember)
                .stage(Stage.builder().stageId(id).build())
                .build();

        StageInvitationDto dto = stageInvitationMapper.toDto(stageInvitation);

        StageInvitation result = StageInvitation.builder()
                .description(stageInvitation.getDescription())
                .build();

        Assertions.assertEquals(result, validator.validateStageInvitation(dto));
    }
}