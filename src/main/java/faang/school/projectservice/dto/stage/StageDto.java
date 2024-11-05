package faang.school.projectservice.dto.stage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {

    public @NotBlank String getStageName() {
        return stageName;
    }

    public @Positive Long getProjectId() {
        return projectId;
    }

    public @NotNull List<@Valid StageRoleDto> getRoles() {
        return roles;
    }

    @Getter
    private Long stageId;

    @NotBlank
    private String stageName;

    @Positive
    private Long projectId;

    @NotNull
    private List<@Valid StageRoleDto> roles;

    @Getter
    private List<Long> executorIds;
}