package faang.school.projectservice.service;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    void testGetResourcesByIds() {
        Resource resource1 = new Resource();
        resource1.setId(1L);
        resource1.setName("Resource 1");

        Resource resource2 = new Resource();
        resource2.setId(2L);
        resource2.setName("Resource 2");

        List<Long> resourceIds = Arrays.asList(1L, 2L);
        when(resourceRepository.findAllById(resourceIds)).thenReturn(Arrays.asList(resource1, resource2));

        List<Resource> result = resourceService.getResourcesByIds(resourceIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Resource 1", result.get(0).getName());
        assertEquals("Resource 2", result.get(1).getName());
        verify(resourceRepository, times(1)).findAllById(resourceIds);
    }

    @Test
    void testGetResourcesByIds_EmptyList() {
        List<Long> resourceIds = Arrays.asList();
        when(resourceRepository.findAllById(resourceIds)).thenReturn(Arrays.asList());

        List<Resource> result = resourceService.getResourcesByIds(resourceIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(resourceRepository, times(1)).findAllById(resourceIds);
    }

    @Test
    void testGetResourcesByIds_ResourceNotFound() {
        List<Long> resourceIds = Arrays.asList(3L);
        when(resourceRepository.findAllById(resourceIds)).thenReturn(Arrays.asList());

        List<Resource> result = resourceService.getResourcesByIds(resourceIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(resourceRepository, times(1)).findAllById(resourceIds);
    }
}