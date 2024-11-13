package faang.school.projectservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime localDateTime;
}
