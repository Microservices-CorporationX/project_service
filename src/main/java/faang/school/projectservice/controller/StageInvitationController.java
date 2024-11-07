package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping("/invite")
    public void sendInvitation(@RequestBody @Valid StageInvitationDto stageInvitationDto) {
        stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PatchMapping("/{stageInvitationId}/accept")
    public void acceptInvitation(@PathVariable @Positive Long stageInvitationId) {
        stageInvitationService.acceptInvitation(stageInvitationId);
    }

    @PatchMapping("/{stageInvitationId}/reject")
    public void rejectInvitation(@PathVariable @Positive Long stageInvitationId,
                                 @NotBlank String reason) {
        stageInvitationService.rejectInvitation(stageInvitationId, reason);
    }

    @GetMapping("/search")
    public List<StageInvitationDto> getUserAllFilteredInvitations(@RequestParam @Positive Long invitedId,
                                                                  @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getAllFilteredInvitations(invitedId, filter);
    }
}
