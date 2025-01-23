package faang.school.projectservice.dto.stageinvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectInvitationDto {
    @NotNull
    private ChangeStatusDto statusDto;

    @NotNull
    @NotBlank
    private String reasonForReject;
}
