package faang.school.projectservice.controller;

import faang.school.projectservice.service.invitation.StageInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stageInvitation")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;;




}
