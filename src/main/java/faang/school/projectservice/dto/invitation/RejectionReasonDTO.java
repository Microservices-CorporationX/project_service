package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class RejectionReasonDTO {

    @NotBlank(message = "Причина отклонения не может быть пустой")
    @Size(max = 255, message = "Причина отклонения слишком длинная")
    private String reason;
}
