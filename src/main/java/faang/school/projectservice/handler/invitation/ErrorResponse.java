package faang.school.projectservice.handler.invitation;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private String errorCode;
}
