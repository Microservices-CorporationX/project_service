package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamAvatarService teamAvatarService;

    @PostMapping("/{id}/upload_avatar")
    public ResponseEntity<?> uploadTeamAvatar(@PathVariable("id") Long id, @RequestParam("file") MultipartFile avatar) {
        return teamAvatarService.uploadTeamAvatar(id, avatar);
    }
}
