package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.filters.DateFilter;
import faang.school.projectservice.filters.InvitationFilter;
import faang.school.projectservice.filters.StatusFilter;
import faang.school.projectservice.filters.UserIdFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.mappers.invitation.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.exceptions.invitation.RejectionReasonMissingException;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
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

        if (invitationDto.getStageId() == null || invitationDto.getInviteeId() == null) {
            log.warn("Отсутствуют Stage ID или Invitee ID в приглашении: {}", invitationDto);
            throw new InvalidInvitationDataException("Stage ID и Invitee ID не могут быть пустыми");
        }

        stageRepository.getById(invitationDto.getStageId());

        teamMemberRepository.findById(invitationDto.getInviteeId());

        StageInvitation invitation = stageInvitationMapper.toEntity(invitationDto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отправлено: {}", savedInvitation);

        return stageInvitationMapper.toDto(savedInvitation);
    }

    public StageInvitationDTO acceptInvitation(Long invitationId) {
        log.info("Принятие приглашения с ID: {}", invitationId);

        StageInvitation invitation = stageInvitationRepository.findById(invitationId);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно принято: {}", updatedInvitation);

        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public StageInvitationDTO rejectInvitation(Long invitationId, String rejectionReason) {
        log.info("Отклонение приглашения с ID: {}. Причина: {}", invitationId, rejectionReason);

        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        if (rejectionReason == null || rejectionReason.isBlank()) {
            log.warn("Причина отклонения обязательна");
            throw new RejectionReasonMissingException();
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setRejectionReason(rejectionReason);

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отклонено: {}", updatedInvitation);

        return stageInvitationMapper.toDto(updatedInvitation);
    }

    public List<StageInvitationDTO> getAllInvitationsForUser(Long userId, StageInvitationStatus status, LocalDate dateFilter) {
        log.info("Получение всех приглашений для пользователя с ID: {}", userId);

        List<StageInvitation> invitations = stageInvitationRepository.findByInvitedId(userId);

        List<InvitationFilter> filters = new ArrayList<>();
        filters.add(new UserIdFilter(userId));

        if (status != null) filters.add(new StatusFilter(status));
        if (dateFilter != null) filters.add(new DateFilter(dateFilter));

        return invitations.stream()
            .filter(invitation -> filters.stream().allMatch(filter -> filter.apply(invitation)))
            .map(stageInvitationMapper::toDto)
            .collect(Collectors.toList());
    }
}
