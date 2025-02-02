package faang.school.projectservice.service;

import faang.school.projectservice.config.AppConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamAvatarService {
    private final MinioService minioService;
    private final AppConfig appConfig;

    public ResponseEntity<?> uploadTeamAvatar(Long id, MultipartFile avatar) {
        int maxSizeInMb = appConfig.getMaxTeamAvatarSize();
        int maxSideLength = appConfig.getMaxTeamAvatarSideLength();

        float avatarSizeInMb = (float) avatar.getSize() / (1024 * 1024);
        if (avatarSizeInMb > maxSizeInMb) {
            throw new IllegalArgumentException("Файл не может весить больше " + maxSizeInMb + " мб");
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                resizeImage(avatar, maxSideLength).toByteArray()
        );

        minioService.uploadFile(inputStream,
                "team/avatar/" + id + "/",
                avatar.getContentType(),
                avatar.getSize());

        return ResponseEntity.ok("OK");
    }

    private ByteArrayOutputStream resizeImage(MultipartFile multipartFile, int maxSideSize) {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(multipartFile.getInputStream())
                    .size(maxSideSize, maxSideSize)
                    .toOutputStream(resultStream);
        } catch (IOException e) {
            log.error("Failed to resize image", e);
            throw new RuntimeException("Failed to resize image: " + e.getMessage());
        }
        return resultStream;
    }
}
