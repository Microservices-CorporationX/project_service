package faang.school.projectservice.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectControllerValidatorTest {

    @InjectMocks
    private ProjectControllerValidator validator;

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testValidateId(long id) {
        assertThrows(DataValidationException.class, () -> validator.validateId(id));
    }
}