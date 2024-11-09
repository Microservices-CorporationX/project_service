package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.validator.stage_invitation.ServiceStageInvitationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.ACCEPTED;
import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.PENDING;
import static faang.school.projectservice.model.stage_invitation.StageInvitationStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private StageService stageService;
    private StageInvitationMapper stageInvitationMapper;
    private StageInvitationRepository stageInvitationRepository;
    private ServiceStageInvitationValidator serviceStageInvitationValidator;

    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        //TODO зачем дана инфа об авторе и статусе, нужно создать новую dto? POSTMAN

        serviceStageInvitationValidator.checkWhetherThisRequestExists(stageInvitationDto.getId());
        serviceStageInvitationValidator.checkTheExistenceOfTheInvitee(stageInvitationDto.getInvitedId());

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitationRepository.save(stageInvitation);

        stageInvitationDto = StageInvitationDto.builder().status(PENDING).build();
        return stageInvitationDto;
    }

    public StageInvitationDto acceptAnInvitation(StageInvitationDto stageInvitationDto) {
        Long id = stageInvitationDto.getStageId();
        StageDto dto = stageService.getById(id);
        dto.getExecutorsId().add(id);

        stageInvitationDto = StageInvitationDto.builder().status(ACCEPTED).build();
        return stageInvitationDto;
    }

    public StageInvitationDto rejectAnInvitation(StageInvitationDto stageInvitationDto) {
        //TODO как указать причину отказа? ГЛЯНУТЬ В БД
        stageInvitationDto = StageInvitationDto.builder().status(REJECTED).build();
        return stageInvitationDto;
    }

    public List<StageInvitationDto> viewAllInvitationsForOneParticipant(Long invitedId) {
        List<StageInvitationDto> stages = new ArrayList<>();
        // фильтрация на уровне базы данных попробовать
        return stages;
    }

    public boolean stageInvitationExist(Long stageInvitationId) {
        return stageInvitationRepository.stageInvitationExist(stageInvitationId);
    }
}
