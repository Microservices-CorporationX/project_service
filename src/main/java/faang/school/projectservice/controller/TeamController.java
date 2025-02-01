package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
@Slf4j
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/{teamId}/upload/avatar")
    public void uploadAvatar(@PathVariable Long teamId,
                       @RequestHeader(name = "x-user-id") Long userId,
                       @RequestParam("file") MultipartFile file) {
        teamService.uploadAvatar(teamId, file, userId);
    }

    @GetMapping("/{teamId}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long teamId) {
        byte[] bytes = teamService.getAvatar(teamId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @DeleteMapping("/{teamId}/delete/avatar")
    public void deleteAvatar(@PathVariable Long teamId,
                       @RequestHeader(name = "x-user-id") Long userId) {
        teamService.deleteAvatar(teamId, userId);
    }
}
