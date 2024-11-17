package faang.school.projectservice.service.stage;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @InjectMocks
    private StageInvitationService stageInvitationService;

    private StageInvitation stageInvitation;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;

    @BeforeEach
    void setUp() {

        // Пример данных для тестов
        stage = new Stage();
        stage.setStageId(1L);

        author = new TeamMember();
        author.setId(1L);

        invited = new TeamMember();
        invited.setId(2L);

        stageInvitation = StageInvitation.builder()
                .id(1L)
                .description("Test Invitation")
                .stage(stage)
                .author(author)
                .invited(invited)
                .build();
    }

    @Test
    @DisplayName("Проверка sendInvitation - ")
    public void testSendInvitation_shouldSaveInvitation() {

        stageInvitationService.sendInvitation(stageInvitation);

        verify(stageInvitationRepository, times(1)).save(stageInvitation);
    }

}
