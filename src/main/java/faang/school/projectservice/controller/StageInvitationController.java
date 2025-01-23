package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/stage-invitation")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping("/create")
    public StageInvitationDto createStageInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.createStageInvitation(stageInvitationDto);
    }

    @PutMapping("/accept/{stageInvitationId}")
    public StageInvitationDto acceptStageInvitation(@Valid @PathVariable Long stageInvitationId) {
        return stageInvitationService.acceptStageInvitation(stageInvitationId);
    }

    @PutMapping("/reject/{participantId}")
    public StageInvitationDto rejectStageInvitation(@Valid @PathVariable Long participantId,
                                                    @NotBlank @RequestBody String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(participantId, rejectionReason);
    }

    @GetMapping("/getAllForOneParticipant/{participantId}")
    public List<StageInvitationDto> getAllInvitationsForOneParticipant(@Valid @PathVariable Long participantId,
                                                                       @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getAllInvitationsForOneParticipant(participantId, filter);
    }
}
