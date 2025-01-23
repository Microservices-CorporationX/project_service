package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRepository stageRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;
    private final List<StageInvitationFilter> stageInvitationFilters;

    public StageInvitationDto createStageInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator
                .validateEqualsId(stageInvitationDto.getAuthorId(), stageInvitationDto.getInvitedId());
        stageInvitationValidator
                .validateInvitedMemberTeam(stageInvitationDto.getAuthorId(), stageInvitationDto.getInvitedId());

        StageInvitation stageInvitation;
        try {
            stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto, teamMemberRepository, stageRepository);
        } catch (EntityNotFoundException e) {
            log.error("Сущность не найдена в базе данных {}", e.getMessage());
            throw e;
        }
        saveStageInvitation(stageInvitation);
        log.info("Было создано новое приглашение с id: {}", stageInvitation.getId());

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptStageInvitation(long invitationId) {
        StageInvitation stageInvitation = stageInvitationRepository.getReferenceById(invitationId);
        TeamMember invited = stageInvitation.getInvited();
        stageInvitationValidator.validateStatusPendingCheck(stageInvitation);

        invited.getStages().add(stageInvitation.getStage());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        saveStageInvitation(stageInvitation);
        log.info("Приглашение присоединиться к этапу с id: {} было принято.", stageInvitation.getId());

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto rejectStageInvitation(long id, String rejectionReason) {
        StageInvitation stageInvitation = stageInvitationRepository.getReferenceById(id);
        TeamMember invited = stageInvitation.getInvited();
        stageInvitationValidator.validateStatusPendingCheck(stageInvitation);

        invited.getStages().remove(stageInvitation.getStage());
        stageInvitation.setRejectionReason(rejectionReason);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        saveStageInvitation(stageInvitation);
        log.info("Приглашение присоединиться к этапу с id: {} было отклонено. \nПричина: {}",
                stageInvitation.getId(), rejectionReason);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> getAllInvitationsForOneParticipant(long participantId,
                                                                       StageInvitationFilterDto filter) {
        Stream<StageInvitation> stageInvitationsFiltered = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation
                        .getInvited()
                        .getId()
                        .equals(participantId));

        return stageInvitationFilters.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(filter))
                .flatMap(stageInvitationFilter -> stageInvitationFilter.apply(stageInvitationsFiltered, filter))
                .map(stageInvitationMapper::toDto)
                .toList();
    }

    private void saveStageInvitation(StageInvitation stageInvitation) {
        try {
            stageInvitationRepository.save(stageInvitation);
        } catch (BusinessException e) {
            log.error("Ошибка при сохранении приглашения на этап в базу данных {}", e.getMessage());
            throw e;
        }
    }
}
