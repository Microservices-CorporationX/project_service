package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ProjectValidator projectValidator;
    private final ResourceValidator resourceValidator;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;

    public ResourceResponseDto addResource(Long projectId, Long userId, MultipartFile file) {
        projectValidator.validateProjectExistsById(projectId);
        teamMemberService.existsById(userId);

        Project project = projectService.getProjectById(projectId);
        //resourceValidator.validateEnoughSpaceInStorage(project, file);



        return null;
    }
}
