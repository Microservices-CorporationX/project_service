package faang.school.projectservice.service;

import dev.mccue.imgscalr.Scalr;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.SubProjectCreateDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.validator.ByteArrayMultipartFile;
import faang.school.projectservice.service.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentRepository momentRepository;
    private final ProjectValidator projectValidator;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final List<SubProjectFilter> subProjectFilters;

    @Value("${cover.max-image-width}")
    private int maxWidth;

    @Value("${cover.max-image-height-horizontal}")
    private int maxHeightHorizontal;

    public ProjectReadDto create(SubProjectCreateDto createDto) {
        projectValidator.validateSubProjectCreation(createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject = projectRepository.save(subProject);
        return projectMapper.toDto(subProject);
    }

    public ProjectReadDto update(SubProjectUpdateDto updateDto) {
        Project project = getProjectById(updateDto.getId());
        projectMapper.updateEntityFromDto(updateDto, project);
        List<Project> subProjects = project.getChildren();

        projectValidator.validateSubProjectStatuses(subProjects, project.getStatus());
        projectValidator.applyPrivateVisibilityIfParentIsPrivate(subProjects, updateDto.getVisibility());

        if (projectValidator.isAllSubProjectsCompleted(subProjects)) {
            addMomentToProject(project);
        }

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectReadDto> getSubProjects(long projectId, SubProjectFilterDto filterDto) {
        Project project = getProjectById(projectId);
        List<Project> subProjects = project.getChildren();

        return subProjects.stream()
                .filter(subProject -> subProjectFilters.stream().filter(filter -> filter.isApplicable(filterDto))
                        .anyMatch(filter -> filter.filterEntity(subProject, filterDto)))
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional
    public void addCoverToProject(long projectId, MultipartFile cover) {
        projectValidator.validateUploadCoverLimit(cover);
        MultipartFile standardizedCover = cover;
        if (!projectValidator.validateCoverResolution(cover)) {
            standardizedCover = resizerCover(cover);
        }
        Project project = getProjectById(projectId);
        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(standardizedCover, folder);
        resource.setProject(project);
        project.setCoverImageId(resource.getKey());

        resourceRepository.save(resource);
        projectRepository.save(project);
    }

    public InputStream getCover(long projectId) {
        String key = getProjectById(projectId).getCoverImageId();
        return s3Service.downloadFile(key);
    }

    @Transactional
    public void deleteCover(long projectId) {
        String key = getProjectById(projectId).getCoverImageId();
        resourceRepository.deleteByKey(key);
        getProjectById(projectId).setCoverImageId(null);
        s3Service.deleteFile(key);
    }

    @SneakyThrows
    private MultipartFile resizerCover(MultipartFile cover) {
        BufferedImage image = ImageIO.read(cover.getInputStream());
        BufferedImage resizerCover = Scalr.resize(
                image,
                Scalr.Method.QUALITY,
                Scalr.Mode.AUTOMATIC,
                maxWidth,
                calculateNewHeight(image));

        ByteArrayOutputStream outputImage = new ByteArrayOutputStream();
        ImageIO.write(resizerCover, "jpg", outputImage);
        byte[] imageBytes = outputImage.toByteArray();

        return new ByteArrayMultipartFile(imageBytes, cover.getOriginalFilename(), cover.getContentType());
    }

    private int calculateNewHeight(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > height) {
            return maxHeightHorizontal;
        } else {
            return maxWidth;
        }
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект с ID "
                        + projectId + " не найден"));
    }

    private Moment getMomentByName(String name) {
        Moment probe = new Moment();
        probe.setName(name);
        Example<Moment> example = Example.of(probe);
        return momentRepository.findOne(example)
                .orElseThrow(() -> new EntityNotFoundException("Момент c названием '" + name + "' не найден"));
    }

    private void addMomentToProject(Project project) {
        List<Moment> moments = project.getMoments();
        moments.add(getMomentByName("Выполнены все подпроекты"));
        project.setMoments(moments);
    }

}
