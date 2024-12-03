package faang.school.projectservice.update.tasks;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.TaskJpaRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.update.TaskUpdate;

public class ParentTaskUpdate implements TaskUpdate {
    private final TaskJpaRepository taskJpaRepository;

    @Override
    public boolean isApplicable(TaskDto taskDto) {
        return taskDto.getParentTaskId() != null;
    }

    @Override
    public void apply(Task task, TaskDto taskDto) {
        task.setParentTask(taskJpaRepository.findById(taskDto.getParentTaskId()));
    }
}
