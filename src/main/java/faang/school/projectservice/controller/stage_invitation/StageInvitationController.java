package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stageinvitation")
public class StageInvitationController {
    private final StageInvitationService invitationService;

    @PostMapping
    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        return invitationService.create(stageInvitationDto);
    }
}
