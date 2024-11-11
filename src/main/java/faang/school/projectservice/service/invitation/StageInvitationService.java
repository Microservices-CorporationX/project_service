package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.RejectionReasonDTO;
import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.exceptions.invitation.InvitationNotFoundException;
import faang.school.projectservice.filters.DateFilter;
import faang.school.projectservice.filters.InvitationFilter;
import faang.school.projectservice.filters.StatusFilter;
import faang.school.projectservice.filters.UserIdFilter;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.mappers.invitation.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.exceptions.invitation.RejectionReasonMissingException;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationJpaRepository stageInvitationRepository;
    private final StageJpaRepository stageRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;

    public StageInvitationDTO sendInvitation(StageInvitationDTO invitationDto) {
        log.info("Получен запрос на отправку приглашения: {}", invitationDto);

        Stage stage = stageRepository.findById(invitationDto.getStageId())
            .orElseThrow(() -> new EntityNotFoundException("Этап не найден"));

        TeamMember invitee = teamMemberRepository.findById(invitationDto.getInviteeId())
            .orElseThrow(() -> new EntityNotFoundException("Участник команды не найден"));

        if (!stage.getProject().equals(invitee.getTeam().getProject())) {
            log.warn("Приглашение не может быть отправлено: участник не принадлежит проекту этапа");
            throw new InvalidInvitationDataException("Участник не принадлежит проекту этапа");
        }


        // Создание и сохранение приглашения
        StageInvitation invitation = stageInvitationMapper.toEntity(invitationDto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        invitation.setStage(stage);
        invitation.setInvitee(invitee);

        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отправлено: {}", savedInvitation);

        return stageInvitationMapper.toDto(savedInvitation);
    }

    @Transactional
    public StageInvitationDTO acceptInvitation(Long invitationId) {
        log.info("Принятие приглашения с ID: {}", invitationId);

        StageInvitation invitation = stageInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new InvalidInvitationDataException("Приглашение не найдено"));

        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        invitation.getStage().getExecutors().add(invitation.getInvitee());

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно принято: {}", updatedInvitation);

        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public StageInvitationDTO rejectInvitation(Long invitationId, RejectionReasonDTO rejectionReasonDto) {
        log.info("Отклонение приглашения с ID: {}. Причина: {}", invitationId, rejectionReasonDto.getRejectionReason());

        StageInvitation invitation = stageInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new InvalidInvitationDataException("Приглашение не найдено"));

        if (rejectionReasonDto.getRejectionReason() == null || rejectionReasonDto.getRejectionReason().isBlank()) {
            log.warn("Причина отклонения обязательна");
            throw new RejectionReasonMissingException("Причина отклонения обязательна");
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setRejectionReason(rejectionReasonDto.getRejectionReason());

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отклонено: {}", updatedInvitation);

        return stageInvitationMapper.toDto(updatedInvitation);
    }

    @Transactional(readOnly = true)
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
