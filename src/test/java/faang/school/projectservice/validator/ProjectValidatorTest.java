package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    ProjectValidator projectValidator;

    ProjectDto projectDto;

    @Mock
    BiFunction<Long, String, Boolean> existsByOwnerUserIdAndName;

    Long ownerId;

    @BeforeEach
    public void setUp() {
        projectValidator = new ProjectValidator();
        projectDto = new ProjectDto();
    }

    @Test
    public void testOwnerIdNotNull() {
        projectDto.setOwnerId(1L);

        assertThrows(IllegalStateException.class,
                () -> projectValidator.validate(projectDto, existsByOwnerUserIdAndName, ownerId));
    }

    @Test
    public void testOwnerIdIsNull() {
        prepareData();

        when(existsByOwnerUserIdAndName.apply(any(), any())).thenReturn(false);
        projectValidator.validate(projectDto, existsByOwnerUserIdAndName, ownerId);
        assertEquals(ownerId, projectDto.getOwnerId());
    }

    @Test
    public void testAlreadyExistsProjectByOwnerUserIdAndName() {
        prepareData();

        when(existsByOwnerUserIdAndName.apply(any(), any())).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> projectValidator.validate(projectDto, existsByOwnerUserIdAndName, ownerId));
    }

    private void prepareData() {
        projectDto.setOwnerId(null);
        ownerId = 1L;
    }
}
