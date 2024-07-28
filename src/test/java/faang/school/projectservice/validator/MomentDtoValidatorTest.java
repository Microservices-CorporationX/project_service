package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MomentDtoValidatorTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private MomentDtoValidator validator;
    private MomentDto dto;

    @BeforeEach
    public void setUp() {
        dto = new MomentDto();
    }

    @Test
    public void ValidateNullNameTest() {
        dto.setName(null);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void ValidateEmptyNameTest() {
        dto.setName("");
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void ValidateNullIdTest() {
        dto.setId(null);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void ValidateNegativeIdTest() {
        dto.setId(-1L);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }
}
