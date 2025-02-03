package faang.school.projectservice.service;

import faang.school.projectservice.config.AppConfig;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        if (!avatar.getContentType().split("/")[0].equals("image")) {
            throw new IllegalArgumentException("Отправленный файл не является картинкой");
        }

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
    public ResponseEntity<?> removeAvatar(Long teamId, Long teamMemberId) {
        validateTeamId(teamId);
        Team team = teamRepository.findById(teamId).get();

        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(teamId, teamMemberId);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("Only manager can remove team's avatar");
        }

        minioService.removeFile(team.getAvatarKey());
        team.setAvatarKey(null);

        return ResponseEntity.ok("OK");
    }

    private ByteArrayOutputStream resizeImage(MultipartFile multipartFile, int maxSideSize) throws IOException {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        log.info("ORIGINAL FILE SIZE {}x{}", originalImage.getWidth(), originalImage.getHeight());

        String formatType = multipartFile.getContentType().split("/")[1];

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        boolean needsResize = false;
        if (width > maxSideSize || height > maxSideSize) {
            needsResize = true;
            if (width > height) {
                height = (int) (height * ((double) maxSideSize / width));
                width = maxSideSize;
            } else {
                width = (int) (width * ((double) maxSideSize / height));
                height = maxSideSize;
            }
        }

        if (!needsResize) {
            resultStream.write(multipartFile.getBytes());
            return resultStream;
        }

        BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, height);
        ImageIO.write(resizedImage, formatType, resultStream);

        log.info("RESIZED FILE SIZE {}x{}", resizedImage.getWidth(), resizedImage.getHeight());
        return resultStream;
    }

    private void validateTeamId(Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() ->
                new EntityNotFoundException("Team with id " + teamId + " doesn't exists"));
    }
}
