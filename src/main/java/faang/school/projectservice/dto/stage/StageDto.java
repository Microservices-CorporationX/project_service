package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {

    @NotNull(message = "stageId не может быть null")
    @Min(value = 1, message = "stageId должен быть больше 0")
    private Long stageId;

    @NotNull(message = "Название этапа не может быть null")
    private String stageName;

    @NotNull(message = "Проект не может быть null")
    private Project project;

    @NotEmpty(message = "Список ролей не может быть пустым")
    private List<StageRolesDto> stageRolesDto;

    @NotEmpty(message = "Список участников команды не может быть пустым")
    private List<TeamMember> executors;

    @NotEmpty
    private List<Task> tasks;
}
