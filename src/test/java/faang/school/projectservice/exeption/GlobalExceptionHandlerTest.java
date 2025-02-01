package faang.school.projectservice.exeption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void testHandleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        String response = handler.handleEntityNotFoundException(ex);
        assertEquals("Entity not found", response);
    }

    @Test
    void testHandleNotUniqueProjectException() {
        NotUniqueProjectException ex = new NotUniqueProjectException("Project already exists");
        String response = handler.handleNotUniqueProjectException(ex);
        assertEquals("Project already exists", response);
    }

    @Test
    void testHandleProjectNotClosableException() {
        ProjectNotClosableException ex = new ProjectNotClosableException("Project cannot be closed");
        String response = handler.handleProjectNotClosableException(ex);
        assertEquals("Project cannot be closed", response);
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Something went wrong");
        String response = handler.handleRuntimeException(ex);
        assertEquals("An unexpected error occurred.", response);
    }
}