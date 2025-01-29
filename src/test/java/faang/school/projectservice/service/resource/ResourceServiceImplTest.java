package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static faang.school.projectservice.utils.ResourcePrepareData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Spy
    private ResourceMapper resourceMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    public void testUploadFile() {
        when(projectRepository.findById(eq(1L))).thenReturn(
                Optional.of(getProject()));
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(s3Service.uploadFile(eq(multipartFile), eq("project")))
                .thenReturn(getResource());
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.findByUserIdAndProjectId(eq(1L), eq(1L)))
                .thenReturn(getTeamMember());
        when(resourceRepository.save(eq(getResultResource())))
                .thenReturn(getResultResource());
        when(projectRepository.save(any())).thenReturn(getProjectResult());

        ResourceDto resourceDto = resourceService.uploadFile(1L, multipartFile);

        assertEquals(resourceMapper.toDto(getResource()), resourceDto);
    }
}