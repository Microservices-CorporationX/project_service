package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StageInvitationController {
    private StageInvitationService stageInvitationService;

    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        log.info("A request has been received to send an invitation {}", stageInvitationDto.getDescription());
        return stageInvitationService.sendAnInvitation(stageInvitationDto);
    }

    public StageInvitationDto acceptAnInvitation(StageInvitationDto stageInvitationDto){
        log.info("A request has been received to accept an invitation {}", stageInvitationDto.getDescription());
        return stageInvitationService.acceptAnInvitation(stageInvitationDto);
    }

    public StageInvitationDto rejectAnInvitation(StageInvitationDto stageInvitationDto){
        log.info("A request has been received to reject an invitation {}", stageInvitationDto.getDescription());
        return  stageInvitationService.rejectAnInvitation(stageInvitationDto);
    }

    public List<StageInvitationDto> viewAllInvitationsForOneParticipant(StageInvitationDto stageInvitationDto){
        log.info("A request has been received to view all invitations with filters for {}", stageInvitationDto.getInvitedId());
        //TODO взять диму для помощи
        return stageInvitationService.viewAllInvitationsForOneParticipant(stageInvitationDto.getInvitedId());
    }
}