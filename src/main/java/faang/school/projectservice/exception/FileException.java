package faang.school.projectservice.exception;

import java.io.IOException;

public class FileException extends RuntimeException {
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}
