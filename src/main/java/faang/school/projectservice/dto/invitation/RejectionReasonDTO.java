package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectionReasonDTO {
    @NotBlank(message = "Причина отклонения не может быть пустой")
    private String reason;
}
