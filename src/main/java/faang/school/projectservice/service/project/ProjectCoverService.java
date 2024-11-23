package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.CoverProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.validator.CoverProjectValidator;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectCoverService {
    private final ProjectService projectService;
    private final CoverProjectValidator coverProjectValidator;
    private final S3Service s3Service;
    private final ImageResizer imageResizer;

    @Value("${services.s3.endpoint}/${services.s3.bucketName}/")
    private String URI;

    public CoverProjectDto getCoverProject(long projectId) {
        Project project = projectService.getProjectById(projectId);

        return CoverProjectDto.builder()
                .URI(project.getCoverImageId())
                .projectId(projectId)
                .build();
    }

    public CoverProjectDto addCoverProject(long projectId, MultipartFile coverImage) {
        Project project = projectService.getProjectById(projectId);

        coverProjectValidator.validation(project);

        System.out.println(coverImage.getContentType());
        if (!coverImage.getContentType().startsWith("image")) {
            throw new IllegalStateException("expected is image");
        }

//        imageResizer.resizeImage(coverImage);

        String key = URI + s3Service.uploadFile(coverImage);
        project.setCoverImageId(key);

        projectService.saveProject(project);

        return CoverProjectDto.builder()
                .URI(key)
                .projectId(projectId)
                .size(coverImage.getSize())
                .build();
    }

    public CoverProjectDto deleteCoverProject(long projectId) {
        Project project = projectService.getProjectById(projectId);
        coverProjectValidator.validation(project);

        project.setCoverImageId(null);
        projectService.saveProject(project);

        return CoverProjectDto.builder()
                .deleted(true)
                .build();
    }
}
