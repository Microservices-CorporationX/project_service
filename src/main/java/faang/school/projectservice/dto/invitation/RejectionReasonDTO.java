package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RejectionReasonDTO {

    @NotBlank(message = "Причина отклонения не может быть пустой")
    private String reason;

    private String rejectionReason;

}
