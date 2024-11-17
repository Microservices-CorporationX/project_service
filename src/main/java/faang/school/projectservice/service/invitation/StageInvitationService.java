package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.mapper.invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;

    public StageInvitationDTO sendInvitation(StageInvitationDTO stageInvitationDTO) {
        log.info("Получен запрос на отправку приглашения: {}", stageInvitationDTO);

        Stage stage = stageRepository.getById((stageInvitationDTO.getStageId()));

        TeamMember invited = teamMemberRepository.findById((stageInvitationDTO.getInvitedId()));
        if (invited == null) {
            log.warn("Приглашаемый участник не найден: {}", stageInvitationDTO.getInvitedId());
            throw new EntityNotFoundException("Приглашаемый участник не найден");
        }

        TeamMember author = teamMemberRepository.findById((stageInvitationDTO.getAuthorId()));
        if (author == null) {
            log.warn("Автор не найден: {}", stageInvitationDTO.getAuthorId());
            throw new EntityNotFoundException("Автор не найден");
        }

        if (!stage.getProject().equals(invited.getTeam().getProject())) {
            log.warn("Приглашение не может быть отправлено: участник не принадлежит проекту этапа");
            throw new InvalidIn
        }
    }
}
