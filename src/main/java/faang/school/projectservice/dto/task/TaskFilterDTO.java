package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskFilterDTO {
    @Positive(message = "Идентификатор проекта не может быть отрицательным")
    @NotNull(message = "Идентификатор проекта не может быть null")
    private Long projectId;
    private Long performerId;
    private String status;
    private String keyword;
    private Long userId;
}
