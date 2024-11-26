package faang.school.projectservice.exception;

import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
}
