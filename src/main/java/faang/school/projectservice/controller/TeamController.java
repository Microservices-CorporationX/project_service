package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamAvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
@Slf4j
public class TeamController {
    private final TeamAvatarService teamAvatarService;

    @PostMapping("/{id}/upload_avatar")
    public ResponseEntity<?> uploadTeamAvatar(@PathVariable("id") Long id, @RequestParam("file") MultipartFile avatar) throws IOException {
        return teamAvatarService.uploadAvatar(id, avatar);
    }

    @DeleteMapping("/{id}/remove_avatar")
    public ResponseEntity<?> removeTeamAvatar(@PathVariable("id") Long id, @RequestParam("requesterId") Long requesterUserId) {
        return teamAvatarService.removeAvatar(id, requesterUserId);
    }
    @GetMapping("/a")
    public String test() {
        return "working";
    }
}
