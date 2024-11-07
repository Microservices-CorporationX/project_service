package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.mappers.invitation.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.exceptions.invitation.InvitationNotFoundException;
import faang.school.projectservice.exceptions.invitation.RejectionReasonMissingException;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;

    // Отправить приглашение
    public StageInvitationDTO sendInvitation(StageInvitationDTO invitationDto) {
        log.info("Получен запрос на отправку приглашения: {}", invitationDto);

        // Валидация данных приглашения
        if (invitationDto.getStageId() == null || invitationDto.getInviteeId() == null) {
            log.warn("Отсутствуют Stage ID или Invitee ID в приглашении: {}", invitationDto);
            throw new InvalidInvitationDataException("Stage ID и Invitee ID не могут быть пустыми");
        }

        // Преобразуем DTO в модель
        StageInvitation invitation = stageInvitationMapper.toEntity(invitationDto);
        invitation.setStatus(StageInvitationStatus.PENDING);

        // Сохраняем приглашение в бд
        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        log.info("Приглашение успешно отправлено: {}", savedInvitation);

        // Возвращаем DTO
        return stageInvitationMapper.toDto(savedInvitation);
    }

    // Принять приглашение
    public StageInvitationDTO acceptInvitation(Long invitationId) {
        log.info("Принятие приглашения с ID: {}", invitationId);

        // Проверяем наличие приглашения с помощью обычной проверки на null
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        if (invitation == null) {
            log.error("Приглашение с ID {} не найдено", invitationId);
            throw new InvitationNotFoundException(invitationId);  // Если приглашение не найдено, выбрасываем исключение
        }

        // Обновляем статус приглашения
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);  // Сохраняем обновленное приглашение

        log.info("Приглашение успешно принято: {}", updatedInvitation);
        return stageInvitationMapper.toDto(updatedInvitation);  // Возвращаем DTO
    }

    // Отклонить приглашение
    public StageInvitationDTO rejectInvitation(Long invitationId, String rejectionReason) {
        log.info("Отклонение приглашения с ID: {}. Причина: {}", invitationId, rejectionReason);

        // Проверяем наличие приглашения
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        if (invitation == null) {
            log.error("Приглашение с ID {} не найдено", invitationId);
            throw new InvitationNotFoundException(invitationId);
        }

        // Проверяем, что причина отклонения не пустая
        if (rejectionReason == null || rejectionReason.isBlank()) {
            log.warn("Причина отклонения обязательна");
            throw new RejectionReasonMissingException();
        }

        // Обновляем статус и причину отклонения
        invitation.setStatus(StageInvitationStatus.valueOf("REJECTED"));
        invitation.setRejectionReason(rejectionReason);
        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);

        log.info("Приглашение успешно отклонено: {}", updatedInvitation);
        return stageInvitationMapper.toDto(updatedInvitation);
    }

    // Получить все приглашения для пользователя
    public List<StageInvitationDTO> getAllInvitationsForUser(Long userId) {
        log.info("Получение приглашений для пользователя с ID: {}", userId);

        // Получаем все приглашения для данного пользователя, где пользователь приглашен
        List<StageInvitation> invitations = stageInvitationRepository.findAll(); // Получаем все приглашения

        // Фильтруем список по invitedId - пользователь, которому отправлено приглашение
        List<StageInvitation> userInvitations = invitations.stream()
            .filter(invitation -> invitation.getInvited() != null && invitation.getInvited().getId().equals(userId))
            .collect(Collectors.toList());

        // Преобразуем сущности в DTO
        List<StageInvitationDTO> userInvitationsDTO = userInvitations.stream()
            .map(stageInvitationMapper::toDto)
            .collect(Collectors.toList());

        log.info("Получено {} приглашений для пользователя с ID: {}", userInvitationsDTO.size(), userId);
        return userInvitationsDTO;
    }
}
