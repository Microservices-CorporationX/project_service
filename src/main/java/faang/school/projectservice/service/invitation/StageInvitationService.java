package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.filters.Invitation.DateFilter;
import faang.school.projectservice.filters.Invitation.InvitationFilter;
import faang.school.projectservice.filters.Invitation.StatusFilter;
import faang.school.projectservice.filters.Invitation.UserIdFilter;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.mappers.invitation.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;

    public StageInvitationDTO sendInvitation(StageInvitationDTO invitationDto) {
        log.info("Получен запрос на отправку приглашения: {}", invitationDto);

        Stage stage = stageRepository.getById(invitationDto.getStageId());
        TeamMember invited = teamMemberRepository.findById(invitationDto.getInviteeId());

        if (!stage.getProject().equals(invited.getTeam().getProject())) {
            log.warn("Приглашение не может быть отправлено: участник не принадлежит проекту этапа");
            throw new InvalidInvitationDataException("Участник не принадлежит проекту этапа");
        }

        StageInvitation invitation = stageInvitationMapper.toEntity(invitationDto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        invitation.setStage(stage);
        invitation.setInvited(invited);

        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отправлено: {}", savedInvitation);

        return stageInvitationMapper.toDto(savedInvitation);
    }

    public StageInvitationDTO acceptInvitation(Long invitationId) {
        log.info("Принятие приглашения с ID: {}", invitationId);

        StageInvitation invitation = stageInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new InvalidInvitationDataException("Приглашение не найдено"));

        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        invitation.getStage().getExecutors().add(invitation.getInvited());

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно принято: {}", updatedInvitation);

        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public StageInvitationDTO rejectInvitation(Long invitationId, StageInvitationDTO stageInvitationDto) {
        log.info("Отклонение приглашения с ID: {}. Причина: {}", invitationId, stageInvitationDto.getRejectionReason());

        StageInvitation invitation = stageInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new InvalidInvitationDataException("Приглашение не найдено"));

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setRejectionReason(stageInvitationDto.getRejectionReason());
        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отклонено: {}", updatedInvitation);

        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public List<StageInvitationDTO> getAllInvitationsForUser(Long userId, StageInvitationStatus status, LocalDate dateFilter) {
        log.info("Получение всех приглашений для пользователя с ID: {}", userId);

        List<StageInvitation> invitations = stageInvitationRepository.findByInviteeId(userId);

        List<InvitationFilter> filters = new ArrayList<>();
        filters.add(new UserIdFilter(userId));

        if (status != null) {
            filters.add(new StatusFilter(status));
        }
        if (dateFilter != null) {
            filters.add(new DateFilter(dateFilter));
        }

        return invitations.stream()
            .filter(invitation -> filters.stream().allMatch(filter -> filter.apply(invitation)))
            .map(stageInvitationMapper::toDto)
            .collect(Collectors.toList());
    }
}
