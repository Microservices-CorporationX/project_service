package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDto {
    private String name;
    private TaskStatus status;
    private Long reporterUserId;

}
