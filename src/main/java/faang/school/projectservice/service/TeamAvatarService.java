package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.AppConfig;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamAvatarService {
    private final MinioService minioService;
    private final AppConfig appConfig;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public ResponseEntity<?> uploadAvatar(Long teamId, MultipartFile avatar) throws IOException {
        int maxSizeInMb = appConfig.getMaxTeamAvatarSize();
        int maxSideLength = appConfig.getMaxTeamAvatarSideLength();

        float avatarSizeInMb = (float) avatar.getSize() / (1024 * 1024);
        if (avatarSizeInMb > maxSizeInMb) {
            throw new IllegalArgumentException("Файл не может весить больше " + maxSizeInMb + " мб");
        }

        validateTeamId(teamId);
        Team team = teamRepository.findById(teamId).get();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                resizeImage(avatar, maxSideLength).toByteArray()
        );

        String key = "team/avatar/" + teamId + "/" + System.currentTimeMillis();
        minioService.uploadFile(inputStream,
                key,
                avatar.getContentType(),
                avatar.getSize());
        team.setAvatarKey(key);

        return ResponseEntity.ok("OK");
    }

    @Transactional
    public ResponseEntity<?> removeAvatar(Long teamId, Long teamMemberUserId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(teamId, teamMemberUserId);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("Only manager can remove team avatar");
        }

        validateTeamId(teamId);
        Team team = teamRepository.findById(teamId).get();

        minioService.removeFile(team.getAvatarKey());
        team.setAvatarKey(null);

        return ResponseEntity.ok("OK");
    }

    private ByteArrayOutputStream resizeImage(MultipartFile multipartFile, int maxSideSize) throws IOException {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        log.info("ORIGINAL FILE SIZE {}x{}", originalImage.getWidth(), originalImage.getHeight());
        try {
            Thumbnails.of(multipartFile.getInputStream())
                    .size(maxSideSize, maxSideSize)
                    .keepAspectRatio(true)
                    .toOutputStream(resultStream);
        } catch (IOException e) {
            log.error("Failed to resize image", e);
            throw new RuntimeException("Failed to resize image: " + e.getMessage());
        }
        BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(resultStream.toByteArray()));
        log.info("RESIZED FILE SIZE {}x{}", resizedImage.getWidth(), resizedImage.getHeight());
        return resultStream;
    }

    private void validateTeamId(Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException("Team with id " + teamId + " doesn't exists"));
    }
}
